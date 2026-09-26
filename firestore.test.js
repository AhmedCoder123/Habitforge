const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "gen-lang-client-0598850939";
const ALICE_UID = "alice_user_1";
const BOB_UID = "bob_user_2";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// 1. Unauthenticated checks
test("Unauthenticated user: cannot read habit logs", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("habit_logs").get());
});

test("Unauthenticated user: cannot write habit logs", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("habit_logs").doc("log_1").set({
    id: "log_1",
    userId: ALICE_UID,
    habitId: "sleep",
    dateKey: "2026-09-26",
    isCompleted: true,
    currentProgress: 1,
    targetProgress: 1,
    createdAt: new Date(),
    updatedAt: new Date(),
  }));
});

// 2. Cross-user isolation
test("Alice: cannot read Bob's habit logs", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).collection("habit_logs").doc("bob_log_1").set({
      id: "bob_log_1",
      userId: BOB_UID,
      habitId: "sleep",
      dateKey: "2026-09-26",
      isCompleted: true,
      currentProgress: 1,
      targetProgress: 1,
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("habit_logs").doc("bob_log_1").get());
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("habit_logs").get());
});

// 3. Alice: can create, read, and update her own habit log
test("Alice: can create and update her own habit log", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const logRef = aliceDb.collection("users").doc(ALICE_UID).collection("habit_logs").doc("alice_log_1");

  await assertSucceeds(logRef.set({
    id: "alice_log_1",
    userId: ALICE_UID,
    habitId: "water",
    habitTitle: "Drink 3 litres water",
    dateKey: "2026-09-26",
    isCompleted: false,
    currentProgress: 2,
    targetProgress: 3,
    karachiDate: "Saturday, Sep 26, 2026",
    createdAt: new Date(),
    updatedAt: new Date(),
  }));

  // Read
  await assertSucceeds(logRef.get());

  // Update
  await assertSucceeds(logRef.update({
    currentProgress: 3,
    isCompleted: true,
    updatedAt: new Date(),
  }));
});

// 4. Alice: can create and read her daily summary
test("Alice: can manage daily summaries for Karachi GMT+5 dateKey", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const summaryRef = aliceDb.collection("users").doc(ALICE_UID).collection("daily_summaries").doc("2026-09-26");

  await assertSucceeds(summaryRef.set({
    dateKey: "2026-09-26",
    userId: ALICE_UID,
    totalScheduled: 12,
    completedCount: 10,
    completionRate: 83.3,
    karachiTimezone: "Asia/Karachi",
    createdAt: new Date(),
    updatedAt: new Date(),
  }));

  await assertSucceeds(summaryRef.get());
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("daily_summaries").get());
});
