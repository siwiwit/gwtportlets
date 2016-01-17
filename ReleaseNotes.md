# 0.9.4beta #

Major changes:

  * GWT 1.7.0 is now supported. The framework will still work on GWT 1.6.x but not on earlier releases. It is developed and tested using GWT 1.7.0
  * The demo has been moved into its own directory and it now has its own build file
  * The documentation on http://www.gwtportlets.org has been moved to a Docbook user manual. This is included in the distribution in PDF and HTML format

Other changes:

  * Fixed: Formbuilder bombs on endRow with no columns
  * Renamed FormBuilder.getFormAsPanel to getFormInPanel
  * Add a "TitlePanel"-like container to provide a "section" style header for a portlet
  * Add 'open page' flag to PageRequest
  * Add method to absolutely position on element over another to LDOM
  * Add TD vertical and horizontal alignment support to FormBuilder
  * Add support for FlexTable subclasses to FormBuilder
  * Add refresh error handling hook to WidgetRefreshHook
  * Add callback to Portlet.refresh
  * Dialog should call setPopupPosition when it is dragged