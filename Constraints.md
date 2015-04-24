# AORSL Constraints #

The following constraints may or may not already be implemented in the AORSL XML schema. There are also some constraints that may overlap like an unique constraint and a key constraint.

| **Element** | **Constraint** | **Name of Constraint** when implemented |
|:------------|:---------------|:----------------------------------------|
| `SimulationParameters` | Name must refer to an existing `SimulationParameterDeclaration` (key/keyref) |  |
| `SimulationParameterDeclaration` | Must be unqiue (key) |  |
|  | It must be prohibited that a parameter can have a name of a default parameter |  |
| `GlobalVariables` | unqiue  |  |
| `StatisticVariables` | unqiue  |  |

StatisticVariables

---

Their sourceObjectType and -Property must be references to exisiting ObjectTypes and their properties.
> -> keyref for the 1st part
> -> assertion for the 2nd part

GlobalVariables

---

If refDataType is used, it must refer to an exisiting EntityType.
> -> keyref

It must be ensured that refDataType and dataType are not used at the same time.
> -> assertion






DataTypes

---

-> see EntityTypes

EntityType

---

An EntityTypes name must be unique.
> -> unique constraint

An EntityTypes superType attribute must refer to another EntityType. Furthermore it must refer to another EntityType of the same type.
> -> key/keyref

EntityType/InitialAttributeValue

---

It must refer to an default attribute or - if existiting - to an attribute of the superType.
> -> assertion (at least for the reference to default attributes)
> -> maybe an key/keyref for the reference to a superType's attribute possible

EntityType/Attribute | EntityType/ReferenceProperty | EntityType/SelfBeliefProperties | BeliefEntityType/BeliefProperties

---

The properties names must ne unique within an entity.
> -> unique constraint

Can it override an already defined property of its supertype. If not it must be prohibited.
> -> maybe unique constraint
> -> otherwise assertion

EntityType/ReferencProperty | BeliefEntity/BeliefReferenProperty

---

It must refer to an existing EntityType (or in case of a BeliefEntityType to an exisiting BeliefEntityType).
> -> key/keyref

EntityTypes/BeliefEntityTypes | EntityTypes/Events

---

Like the "top-level" EntityTypes their names must be also unique but just within the range of their parent.
> -> unique

EntityTypes/Rules

---

Their names must be unique within the range of their parent.
> -> unqiue






Function | SubjectiveFunction | GlobalFunction

---

Its resultType must be wether a datatype or a reference to an existing EntityType, ComplexDataType or Collection.
> -> assertion
> -> or if devided into to two kinds of resultType (resultDatatype and resultObjecttype) an key/keyref might be possible for the object part. an assertion would be useful to ensure that not both resultTypes are used at the same time.

Function/Parameter | SubjectiveFunction/Parameter

---

It must refer to an existing EntityType, ComplexDataType or Collection. Or its type must be a datatype.
> -> assertion
> -> if devided into to kinds of parameter (datatypeParameter and objectParameter) an key/keyref might be possible for the object part

Their names must be unique within the functions range.
> -> unique







SpaceModel

---

the GridCellProperties must be keys. It may be also required that they are distinvt to the default spacemodel parameters
> -> key
> -> assertion for the 2nd part

Collections

---

-> see Objects





EnvironmentRules/WHEN

---

The type attributes must refer to an existiting message/physicalObject/event-Type.
> -> key/keyref
> -> assertion for built-in types (alternativly another parameter for those)


The variables must be unique within the rules range and they must be different to global variables.
> -> unqiue (see also global variables)


EnvironmentRules/FOR

---

The type attributes must refer to an existing objectType.
> -> key/keyref
> -> asserting for built-in types (alternativly another parameter for those)

The variables must be unique within the rules range and they must be different to global variables.
> -> unqiue (see also global variables)


EnvironmentRules/UPDATE-ENV/Update|IncrementGlobalVaribale

---

Thy must refer to an existing global variable.
> -> key/keyref


EnvironmentRules/UPDATE-ENV/UpdateObject

---

The variables type and the called slots must refer to an existing type/property.
> -> assertion
> -> assertion (also for ids when expression language is used)

The variables name must be unique within the rules but just the components part.
> -> unique (for component level)
> -> assertion (for rule level)


EnvironmentRules/UPDATE-ENV/UpdateGridCell

---

A Slot must refere to an existing property.
> -> key/keyref


The variables name must be unique within the rules but just the components part.
> -> unique (for component level)
> -> assertion (for rule level)


EnvironmentRules/UPDATE-ENV/UpdateObjects

---

The objectType must refer to an exisiting type.
> -> key/keyref
> -> assertion for built-in (or extra attribute)

The slot must refer th a types property.
> -> assertion

The variables name must be unique within the rules but just the components part.
> -> unique (for component level)
> -> assertion (for rule level)


EnvironmentRules/UPDATE-ENV/AddObjectToCollection|RemoveObjectFromCollection

---

The collectionName must refer to an exisiting collection.
> -> key/keyref

The variables name must be unique within the rules but just the components part.
> -> unique (for component level)
> -> assertion (for rule level)



EnvironmentRules/UPDATE-ENV/Increment|Decrement

---

To what do they refer?
Why value restricted to long and not tu "numeric"?

The property must refer to an exitising one.
> -> key/keyref


EnvironmentRules/UPDATE-ENV/UpdateStatisticsVariable

---

The variable must refer to an exitising one.
> -> key/keyref



todo:
EnvironmentRules/Create|Destroy
- AgentRules
- InitialState (Objects)
- Views | Scales