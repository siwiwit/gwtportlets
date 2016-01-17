>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<

## Chapter 9. Integrating with Spring ##

Spring is a platform for building Enterprise Java applications. It provides lifecycle management and wiring for application components (among other features). A Spring / GWT Portlets application will typically do the following:

  * Annotate WidgetDataProviders with `@Service` or `@Repository` to make them Spring beans
  * Make the application PageProvider a Spring bean with an `@Autowired` WidgetDataProvider[.md](.md) property to discover all WidgetDataProviders

A complete demo application using Spring and JPA (Java Persistence API) will added to the distribution soon.

>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<