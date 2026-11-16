# openflexo-emf — Backlog

Features and evolutions to develop. Each entry says what problem it solves and when it is done. Bugs that are known and not fixed go to `KNOWN_DEFECTS.md`, not here.

Identifiers are stable and never reused: `EMF-F-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## FML runtime errors

### EMF-F-1 — The emf adapter raises typed FML errors instead of returning null after a warning or a printed stack  ·  `DONE`

**Problem.** Part of `CORE-F-11` phase 6 (`openflexo-core/BACKLOG.md`), done with the other remaining adapters in one batch (asked by the user, 2026-11-16), with the rules settled on the first adapters (xlsx, xml, jdbc, rest, formod, docx): the edition actions caught the failures of their evaluations (four catches printing the stack, or a log), and went on with null after a warning.

**Rules applied (decided with the user on the first adapters).** Evaluations through `FMLEvaluation`; every `execute` / `performExecute` declares `throws FMLExecutionException`; a null receiver, model, document, name or required input is an `FMLNullReferenceError`; a select from a null source is an `FMLNullReferenceError` naming the select; a foreseen failure of the technology an `FMLTechnologyError` with a message of its own; a `ModelDefinitionException` an `FMLInternalError`; a cancelled loading an `FMLCancellation`; a typed failure of `AbstractCreateResource` rethrown unchanged; a log just before a throw removed (the failure is reported once, by the editor); the `System.out` traces of the actions removed. Scope: the paths that FML execution goes through; loading and conversion code left.

**Done (2026-11-16).**
- `AddEMFObjectIndividual`: no model is an `FMLNullReferenceError`, a new value of an enumeration an `FMLTechnologyError` (null after a warning).
- `SelectEMFObjectIndividual`: a null model is an `FMLNullReferenceError` (null before, then the generic error of the core).
- `AbstractCreateEMFResource`: typed failures (no metamodel resource, a resource center that is not on the file system, a failing creation); an unchecked exception is no longer wrapped into an untyped error (a defect).
- `RemoveEMFObjectIndividual` is a stub returning null: left.
- The conditions and values of the ontology assertions are strict since `XML-F-1` (core, `flexo-ontology`).

**Known limits.**
- `AddEMFObjectIndividual` puts the new object in the model before evaluating its assertions: an assertion that now fails leaves the object in the model.

**After review (batch review).** No initial model to copy (a failing duplication, a metamodel outside a jar) is an `FMLTechnologyError`
checked before the new resource is created (it was an NPE after the creation, leaving an empty resource registered); a
`ModelDefinitionException` is an `FMLInternalError`, a cancelled loading an `FMLCancellation`.

**Verified (2026-11-16).** emf-ta-test 57 (55 run) green; ECJ compiles emf-ta. No new test.
