## SmartGwt Integration ##

[SmartGwt](http://code.google.com/p/smartgwt/) is a rich widget library which includes client and server side data integration.
The library is a wrapper for the [SmartClient](http://www.smartclient.com/) javascript library.
The client side widget library is released under LGPL and is free to use.
The server side integration is only available in the paid for versions of SmartClient.

The portlet framework has an extension library which allows SmartGwt widgets to be used within a portlet. This library provides classes and methods which integrate SmartGwt's data sources with the portlet framework so that SmartGwt requests can be sent from a portlet to its data provider. The library is provided as `gwt-portlets-smartgwt.jar`. There is also a SmartGwt portlets demo available which demonstrates how to use the library.

Should you use SmartGwt widgets in a portlet, it is recommended that you only use SmartGwt widgets in that portlet. Do not mix and match widgets between that library and other widget libraries.

## Creating a SmartGwt Portlet ##
In the SmartGwt Portlets demo, the `CompoundEditorPortlet` is a simple implementation of a CRUD application.
First, we need some sort of data which we can perform our CRUD operations on. In this example we will be using a simple `TownRecord`. This object must be serializable since this is the data transfer object which will be used to transfer records to and from the server.
```
public class TownRecord implements IsSerializable {
    Integer id;
    String name;
    Date date;
}
```

SmartGwt uses data sources to represent how data is represented in its widgets. The data source specifies tables, fields, relationships and even restrictions on fields. See the SmartGwt documentation for more information. The `SmartPortletDataSource` class must be used to implement a DataSource in the portlets framework.
```
public class TownDataSource extends SmartPortletDataSource {
    public TownDataSource() {
        super();
        DataSourceField field;
        field = new DataSourceIntegerField ("id", "Id");
        field.setPrimaryKey(true);
        field.setRequired(false);
        addField(field);
        field = new DataSourceTextField ("name", "Name");
        field.setRequired(true);
        addField(field);
        field = new DataSourceDateField ("date", "Date");
        field.setRequired(false);
        addField(field);
    }

    public static TownRecord create(Record from) {
        TownRecord to = new TownRecord();
        to.setId(from.getAttributeAsInt("id"));
        to.setName(from.getAttributeAsString("name"));
        to.setDate(from.getAttributeAsDate("date"));
        return to;
    }

    public static Record createRecord(TownRecord from) {
        Record to = new ListGridRecord();
        to.setAttribute("id", from.getId());
        to.setAttribute("name", from.getName());
        to.setAttribute("date", from.getDate());
        return to;
    }
}
```

Once a data source has been defined, we can create a `SmartPortlet`:
```
    public CompoundEditorPortlet() {
        dataSource = new TownDataSource();
        dataSource.setPortlet(this);
        cEditor = new CompoundEditor(datasource);
        cEditor.setDataSource(datasource);
        initWidget(cEditor);
    }
```
From this example, you can see that the data source must be bound to a portlet. This restriction is necessary because the portlet is responsible for the data transfer between the client and the server. When the data source receives a request, it needs to know which portlet to use to facilitate that request.

After the data source has been created, it can be used with a SmartGwt widget. In the above code, we see the data source is passed to the `CompoundEditor` widget. This widget is a child class of the `CompoundEditorPortlet`.
SmartGwt uses excessively high (200k+) z-index values for its HTML elements. This causes all portlet items, including menus and maximised portlets, to be rendered underneath them. To stop this behaviour, the `SmartPortlet.initWidget` method is overridden and will set the z-index of the given widget to zero. As long as all the child HTML elements are relative to the parent, this will render all portlet items correctly.

So we now have a portlet and a SmartGwt widget inside it which has been bound to our data source. Now we need to implement the portlet's factory so that we have some methods to transfer data to and from the server. The `SmartPortletFactory` class must be used to implement the smart portlet's factory.
```
public static class Factory extends SmartPortletFactory<CompoundEditorPortlet> {
    @DoNotSendToServer
    public List<TownRecord> data;
    public TownRecord record;

    public CompoundEditorPortlet createWidget() {
        return new CompoundEditorPortlet();
    }

    public void refresh(CompoundEditorPortlet p) {
        super.refresh(p);
        p.restore(this);
    }

    public void executeAdd(SmartPortletDataSource dataSource, DSRequest request) {
        super.executeAdd(dataSource, request);
        updateSelectedRecord(request);
    }

    public void executeUpdate(SmartPortletDataSource dataSource, DSRequest request) {
        super.executeUpdate(dataSource, request);
        updateSelectedRecord(request);
    }

    public void executeRemove(SmartPortletDataSource dataSource, DSRequest request) {
        super.executeRemove(dataSource, request);
        updateSelectedRecord(request);
    }

    private void updateSelectedRecord(DSRequest request) {
        JavaScriptObject data = request.getData();
        final ListGridRecord rec = new ListGridRecord(data);
        record = TownDataSource.create(rec);
    }
}
```

The factory has the usual `createWidget` and `refresh` methods. It also has some additional execute methods which you can override to handle certain types of messages. These methods are called by the data source when one of its bound widgets makes a SmartGwt request. If you add widgets which can perform these operations, you should override these methods. In this case, each method calls the same function which takes the data within the data source request and converts this to a `TownRecord` object and stores this in the factory. This record will be sent to the portlet's data provider along with other information about the request.

The data provider is similar to the usual portlet data provider except for the addition of methods which are used to handle CRUD operations:
```
public class CompoundEditorDataProvider extends SmartWidgetDataProvider<CompoundEditorPortlet.Factory> {
    private static final SmartFilter<TownRecord> filter 
        = new SmartFilter<TownRecord>(CountryRecord.class);

    public Class getWidgetFactoryClass() {
        return CompoundEditorPortlet.Factory.class;
    }

    public void executeFetch(CompoundEditorPortlet.Factory f) {
        List<TownRecord> list = getAllTownData(); // Our data store is a simple list
        // If there is filtering
        if (f.getCriteria() != null) {
            try {
                list = filter.filter(f.getCriteria(), list);
            } catch (SmartException e) {
                e.printStackTrace();
            }
        }
        // If there is sorting
        if (f.getSort() != null) {
            Collections.sort(list, 
                new SmartComparator<TownRecord>(TownRecord.class, f.getSort()));
        }

        f.data = new ArrayList<TownRecord>(
            list.subList(
                Math.min(f.getStartRow(), list.size()), 
                Math.min(f.getEndRow(), list.size())
            ));
        f.setTotalRows(list.size());
    }

    public void executeAdd(CompoundEditorPortlet.Factory f) {
        // Add f.record
    }

    public void executeUpdate(CompoundEditorPortlet.Factory f) {
        // Update the entry with id f.record.getId();
        // Set f.record to the updated record or null if the record could not be found
    }

    public void executeRemove(CompoundEditorPortlet.Factory f) {
        // Remove the entry with id f.record.getId();
        // Optionally, set f.record to the entry which was removed or null
    }
}
```

The most important operation is the fetch operation. This is where all your data is retrieved, sorted and filtered. In this method, you can simply fetch all your data and return that to the client without limiting, filtering or sorting it. The SmartGwt data source on the client will then apply the sorting and filtering on the client side. This of course isn't optimal since you may have a lot of data which is sent to the client and only a little of it is actually shown. The preferred method is to use the limiting values given in the factory. To do this, you must limit your returned values by the start and end rows given in the `SmartPortletFactory`. In addition to this, you must also set the total rows value so that the client knows how many rows there are to fetch in total.

Filtering parameters are sent using `CriteriaDto` objects. These objects can represent any simple and advanced filtering parameters and operators which can be nested. The `SmartFilter` class provides methods to help filter results based on a list, or to even generate SQL WHERE queries.

Sorting specifiers are sent in the factory's sort field. This is an array of `SortSpecifierDto` objects which allow for any sorting of the data.

If you implement the SmartGwt paging, you must also implement the filtering and sorting in order for your data to be represented accurately on the client.

Once the data provider sets the data to return to the client, the factory's refresh method will be called. In our above example, this calls the Portlet's restore method.
```
    private void restore(Factory f) {
        DSRequest request = removeRequest(f);
        if (request != null) {
            switch (request.getOperationType()) {
                case FETCH:
                    fetchResponse(request, f);
                    break;
                case ADD:
                case UPDATE:
                    updateResponse(request, f, true);
                    break;
                case REMOVE:
                    updateResponse(request, f, false);
                    break;
            }
        }
    }

    private void fetchResponse(DSRequest request, Factory f) {
        DSResponse response = SmartPortlet.createResponse(request, f);
        Record[] list = new Record[f.data.size()];
        for (int i = 0; i < list.length; i++) {
            list[i] = TownDataSource.createRecord(f.data.get(i));
        }
        response.setData(list);
        dataSource.processResponse(request.getRequestId(), response);
    }

    private void updateResponse(DSRequest request, Factory f, boolean select) {
        DSResponse response = SmartPortlet.createResponse(request);
        Record[] list = new Record[1];
        list[0] = TownDataSource.createRecord(f.record);
        response.setData(list);
        dataSource.processResponse(request.getRequestId(), response);
        if (select) {
            cEditor.selectSingleRecord(list[0]);
        }
    }
```

In this method, the type of request is determined and handled appropriately. In a fetch request, all the records sent from the factory are converted into SmartGwt objects and the appropriate response is created and passed to the data source.

The implementation of the `SmartPortlet` requests allows for any number of requests to be pending at any time.

## Future Work ##
  * Use java annotations to define a data source from the DTO object directly.
  * Remove the data source binding to a single portlet (if possible).
  * Look into a better solution for the high z-index problem.
