# New Features Planned for the Future #

  * Support continuous property value changes by introducing a new Property subelement (`ContinuousChangeExpr`) such that the value of such a property is re-computed at each simulation step according to this expression
  * Support constructor definitions in a `ComplexDataType` (with elements `ComplexDataType`/`Constructor`, `Constructor`/`Parameter` and `Constructor`/`Body`)
  * Support a new pre-defined `isAvailableAsResource` attribute for objects, such that activities requiring a resource of that type may not use it, if it is not available (they may either allocate another resource of that type or wait for the availability of one)
  * Introduce component types definable for any object type
  * Support the transportation of physical objects by physical agents by introducing the following:
    * a built-in action event type `LoadTransportObject` with a reference property `transportObject`
    * a built-in action event type `UnloadTransportObject` with a reference property `transportObject`
    * a new list-valued built-in attribute `transportObjects` such that these objects are moved along with the agent (possibly by injecting code for moving them into the setX/setY methods of the physical agent object)
  * Support a distinction between _base types_ and _role types_ (according to the Unified Foundational Ontology) such that an object/agent is always of a unique base type (e.g. `Person`), but may be an instance of several role types (e.g. `Lecturer` and `Researcher`)
  * Introduce a concept of _institutional agent_ (corresponding to 'group', 'organization', 'organizational unit', and bpmn:pool) for
    * aggregating a number of agents as _subagents_
    * defining _institutional roles_ that are assigned to its subagents
    * delegating the handling of events and the performance of activities to subagents
  * Support multiple schemas and multiple versions of schemas

# New Features in AORSL 0.8.4 (planned release date: March 2011) #

  1. Validation and code generation are now done via a Web service, so the developer has to be online for building a simulation (not for running it)
  1. Introduce a new element `UPDATE-AGT`/`Call` for invoking a procedure that may be used for complex updates of the agent state
  1. Introduce a new element `UPDATE-AGT`/`MultiValuedSlot` for dealing with multi-valued properties (adding items to a value list, removing items from a value list and assigning values to an item property)
  1. Add an Initial State User Interface for allowing authors to edit the initial state of a simulation scenario before starting it
  1. New architecture for the simulation management system: the code generation is now a (JAX-WS) Web service using Saxon EE for validation (this allows using XML Schema 1.1 constraints in the AORSL schema)

# New Features in AORSL 0.8.3 (release date: May 2010) #

  1. Improved syntax for the `SpaceModel` element
  1. Improved syntax for views
  1. Introduce composite/embedded views
  1. Introduce a new `UserInterface` container element containing the subelements `StatisticsUI` for the statistsics user interface and `AnimationUI` for the animation user interface (including views for visualization and agent control for interactive simulation)
  1. Introduce further built-in physics attributes:
    1. the orientation (angular position) in terms of `rotationAngleX`, `rotationAngleY`  and `rotationAngleZ`, see also [Wikipedia article](http://en.wikipedia.org/wiki/Yaw,_pitch_and_roll)
    1. the angular velocity vector omega: `omegaX`, `omegaY`, `omegaZ`
    1. the angular acceleration vector alpha: `alphaX`, `alphaY`, `alphaZ`
    1. material type (determining _restitution_ and _friction_)

# New Features in AORSL 0.8.2 (release date: December 2009) #

  1. Improved syntax for statistics variables (allowing to assign them one of a possible number of sources)
  1. Support statistics variables representing the size of the extension of an object type
  1. Support statistics variables representing the utilization of a (type of) resource object
  1. Introduce a special subelement `ON-EACH-SIMULATION-STEP` both for EnvironmentRule and for AgentRule, as an alternative to `WHEN`, for rules that are executed at each step
  1. Suppport FOR loops over supertypes through code generation (e.g. if Animal is the supertype of Predator and Prey, a FOR loop over Animal will create two loops: one over Predator  and another one over Prey)


# New Features in AORSL 0.8.1 (release date: September 2009) #

  1. The `SimulationScenario/@version` attribute is mandatory and fixed to the schema version.
  1. There is now a shrinked schema ("ersl/ERSL-0-8-1.xsd") for basic discrete event simulation without agents ("ER Simulation")
  1. The rule language has been improved by introducing the two new elements `EnvironmentRule/DO` and `EnvironmentRule/THEN`. The new `DO` element allows to trigger `UPDATE-ENV` and `SCHEDULE-EVT` blocks without checking any further condition. The new `THEN` element is just a new container (for `UPDATE-ENV` and `SCHEDULE-EVT` blocks) for getting a nice WHEN-FOR-DO-IF-THEN-ELSE rule structure.
  1. There is now a new `UPDATE-ENV/UpdateGridCells` element for allowing update loops over the entire grid in cellular automata-style models.
  1. A `DataTypes/ComplexDataType` element may now have a new `Def` subelement of type `OpaqueExpression` for allowing arbitrary class definitions that can serve as data types for a `ComplexDataProperty`.
  1. There is now a new `UPDATE-AGT/UpdateComplexDataPropertyValue` element with `Argument` subelements  of type `OpaqueExpression` for allowing to call an update procedure of a `ComplexDataType` for updating the value of a `ComplexDataProperty`.