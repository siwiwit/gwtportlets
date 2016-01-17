>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<

## Chapter 2. Portlets ##

A portlet is a GWT Widget (it extends Composite) with features that make it easier to build GWT applications composed of decoupled components:

  * It can externalize its state into an instance of a PortletFactory subclass that can recreate the Portlet and/or restore its state
  * It can "refresh" itself by sending a PortletFactory instance to the server for update (e.g. from a database) and restoring its state using the returned factory to show the new data i.e. the factory is used as a DTO (Data Transfer Object)
  * It has a user friendly title
  * It may be able open a dialog to configure itself
  * It notifies its parent (and its parent's parent recursively) when its state changes (e.g. busy with refresh, title changed) for automatic display of AJAX loading pizza's etc.
  * It can be positioned absolutely and is aware of its position and size and hence can use scrolling regions effectively

![http://gwtportlets.googlecode.com/svn/wiki/doc/img/portlet_heirachy.gif](http://gwtportlets.googlecode.com/svn/wiki/doc/img/portlet_heirachy.gif)

**Figure 2.1. Portlet Class Heirachy**

## 2.1. Hello World Portlet ##

This section describes the different parts of the "hello world"portlet from the main demo (demos/main). The Portlet class is as follows:

```
package main.client.ui;

import com.google.gwt.user.client.ui.*;
import org.gwtportlets.portlet.client.*;
import org.gwtportlets.portlet.client.ui.*;

public class HelloWorldPortlet extends Portlet {

    private String serverTime;

    private Label label = new Label();

    public HelloWorldPortlet() {
        initWidget(label);
    }

    private void restore(Factory f) {
        serverTime = f.serverTime;
        label.setText("Hello World, the time on the server is " + serverTime);
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    public static class Factory extends PortletFactory<HelloWorldPortlet> {

        @DoNotSendToServer public String serverTime;

        public Factory() { }

        public Factory(HelloWorldPortlet p) {
            super(p);
            serverTime = p.serverTime;
        }

        public void refresh(HelloWorldPortlet p) {
            super.refresh(p);
            p.restore(this);
        }

        public HelloWorldPortlet createWidget() {
            return new HelloWorldPortlet();
        }
    }
}
```

This portlet displays its message using a single label field. Since it is a Composite it calls initWidget(label) in the constructor.

The static inner class `Factory extends PortletFactory {...}` has a single field String serverTime to hold the state of the portlet. This field is marked with the @DoNotSendToServer annotation which causes the framework to set it to null before the factory is sent to the server, reducing the amount of data transfered.

The portlet method WidgetFactory createWidgetFactory() {...} creates a instance of the factory class externalizing the state of the portlet. This method is specified by the WidgetFactoryProvider interface.

The refresh(HelloWorldPortlet p) {...} method in the factory restores the state of the portlet by calling its restore(Factory f) {...} method. The restore method copies data out of the factory fields into the portlet fields (serverTime) and sets the text on the label to update the GUI.

PortletFactory instances are filled with data on the server side by WidgetDataProvider implementations. Data providers are named after the portlet they supply data for by convention (HelloWorldDataProvider for HelloWorldPortlet etc.). Note that a Portlet is not required to have a corresponding WidgetDataProvider (e.g. the PageTitlePortlet in the framework).

Here is the "hello world" data provider:

```
package main.server;

import main.client.ui.HelloWorldPortlet;
import org.gwtportlets.portlet.server.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HelloWorldDataProvider
        implements WidgetDataProvider<HelloWorldPortlet.Factory> {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z");

    public Class getWidgetFactoryClass() {
        return HelloWorldPortlet.Factory.class;
    }

    public void refresh(HelloWorldPortlet.Factory f, PageRequest req) {
        f.serverTime = DATE_FORMAT.format(new Date());
    }
}
```

The getWidgetFactoryClass() method just returns the factory class that the data provider populates.

The refresh(HelloWorldPortlet.Factory f, PageRequest req) method populates the factory with the time on the server. The PageRequest contains information derived from the current "history token" on the client (the part after the # in the URL). In particular if the history token contains "parameters" then these are available through the PageRequest.

Example: If the history token at the time of refresh is "#hello\_world?foo=bar" then req.get("foo") will return "bar". A more complex data provider might use this parameter and information from the factory to execute a database query.

The page request also includes the HttpServletRequest and HttpServletResponse for the call.

## 2.2. Simple CRUD Portlet ##

This section describes the different parts of the "simple crud"portlet from the main demo (demos/main). This portlet displays a list of contacts on a grid with buttons to add, edit and delete contacts.

The static inner class Contact is a data transfer object (DTO) which is used to transfer data between the portlet and the data povider.

```
public class SimpleCrudPortlet extends Portlet {
...
    public static class Contact implements Serializable {
      public int contactId;
      public String name;
      public String mobile;
    }
...
}
```

The portlet factory has an array of Contacts which is populated by the data porvider on the server side and displayed by the portlet. This field is annotated `@DoNotSendToServer` to avoid sending this data back to the server when refreshing the Portlet.

The portlet factory also has two other attributes` int deleteContactId` and `Contact update` these are used when deleting, adding and editing a Contact. Note that these fields are annotated `@DoNotPersist` so they are not stored in XML page files.

```
public class SimpleCrudPortlet extends Portlet {
...
    public static class Factory extends PortletFactory<SimpleCrudPortlet> {

      @DoNotSendToServer
      public Contact[] contactList;
      @DoNotPersist
      public int deleteContactId;
      @DoNotPersist
      public Contact updateContact;
      ...
}
```

The data provider's `refresh(SimpleCRUDPortlet.Factory f, PageRequest req) {...}` populates the contactList field in the factory with a list of contacts. This list is stored in the session on the server to simulate a database.

```
public class SimpleCrudDataProvider
      implements WidgetDataProvider<SimpleCrudPortlet.Factory> {
  ...
  public void refresh(SimpleCrudPortlet.Factory f, PageRequest req) {
      ...
      List<SimpleCrudPortlet.Contact> list = getContacts(req); // get from session
      ...
      f.contactList = list.toArray(new SimpleCrudPortlet.Contact[list.size()]);
      ...
  }
  ...
}
```

The portlet's restore method loops through the ContactList and adds the to the FlexTable.

```
public class SimpleCrudPortlet extends Portlet {
  private FlexTable grid;
  ...
  private void restore(Factory f) {
      contactList = f.contactList;
      ...
      for (int i = 0; i < contactList.length; i++) {
          Contact contact = contactList[i];
          int row = i + 1;
          grid.setText(row, 0, "" + contact.contactId);
          grid.setText(row, 1, contact.name);
          grid.setText(row, 2, contact.mobile);
      }
  }
...
```

Adding and editing a Contact is done by the same dialog. The add method opens the dialog passing a new Contact to the dialog. The edit method gets the selected contact on the grid and passes it to the dialog.

```
private void showContactDialog(final Contact c) {
    final Dialog dlg = new Dialog();
    final TextBox name = new TextBox();
    final TextBox mobile = new TextBox();

    boolean adding = c.contactId == 0;
    dlg.setText((adding ? "Add" : "Edit") + " Contact");
    name.setText(c.name);
    mobile.setText(c.mobile);

    FormBuilder fb = new FormBuilder();
    fb.label("Name").field(name).endRow();
    fb.label("Mobile").field(mobile).endRow();

    dlg.setWidget(fb.getForm());
    dlg.addButton(new CssButton("OK", new ClickHandler() {
        public void onClick(ClickEvent clickEvent) {
            if (name.getText().length() == 0 || mobile.getText().length() == 0) {
                Window.alert("Name and Mobile are required");
                return;
            }
            Contact dto = new Contact();
            dto.contactId = c.contactId;
            dto.name = name.getText();
            dto.mobile = mobile.getText();
            // send the Contact to be updated to the server via refresh
            Factory f = new Factory(SimpleCrudPortlet.this);
            f.update = dto;
            refresh(f, dlg);
            // dialog hides itself onSuccess and ignores onFailure
            // which is handled by main.client.Demo
        }
    }));
    dlg.addCloseButton("Cancel");
    dlg.showNextTo(grid);
}
```

A new Contact DTO is created using the data from the Dialog when OK is clicked. This is attached to the update field of the Factory and sent to the server via a refresh call. The dialog itself is passed to the refresh method as the AsyncCallback. It hides itself in onSuccess and does nothing in onFailure as errors are handled globally in main.client.Demo.

The SimpleCrudDataProvider handles the refresh as shown below:

```
public class SimpleCrudDataProvider
        implements WidgetDataProvider<SimpleCrudPortlet.Factory> {
    ...
    public void refresh(SimpleCrudPortlet.Factory f, PageRequest req) {
        List<SimpleCrudPortlet.Contact> list = getContacts(req);
        if (f.update != null) {
            update(list, f.update);
        }
        if (f.deleteContactId > 0) {
            delete(list, f.deleteContactId);
        }
        save(req, list);
        f.contactList = list.toArray(new SimpleCrudPortlet.Contact[list.size()]);
    }
    ...
}
```

Deleting a Contact is done when the delete button is clicked, the deleteContactId is set to the selected contact on the grid. The data provider then removes the contact from the list.

The update method in SimpleCrudDataProvider throws an IllegalArgumentException on duplicate names. This causes the refresh to fail and an error alert is displayed on the client side.

Using the Portlet refresh machanism for updates and deletes removes the need for additional RPC methods and associated complexity.

>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<