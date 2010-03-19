package smartgwt.server.data;

import org.apache.log4j.Logger;
import org.gwtportlets.portlet.smartgwt.client.DataTransferObject;
import org.gwtportlets.portlet.smartgwt.client.SmartPortletFactory;
import org.gwtportlets.portlet.smartgwt.server.DataProviderObject;
import org.gwtportlets.portlet.smartgwt.server.SmartComparator;
import org.gwtportlets.portlet.smartgwt.server.SmartException;
import org.gwtportlets.portlet.smartgwt.server.SmartFilter;
import smartgwt.client.data.CountryDto;
import smartgwt.client.data.TownDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class TownDpo implements DataProviderObject {
    private static final Logger log = Logger.getLogger(TownDpo .class);
    private static final SmartFilter<TownDto> filter = new SmartFilter<TownDto>(CountryDto.class);
    static List<TownDto> list;

    public String getDataSourceId() {
        return TownDto.DATA_SOURCE_ID;
    }

    public List<DataTransferObject> fetchData(SmartPortletFactory f) {
        List<TownDto> list = TownDpo.list;
        log.info("Doing fetch: " + f.getStartRow() + " -> " + f.getEndRow());
        if (f.getSort() != null) {
            Collections.sort(list, new SmartComparator<TownDto>(TownDto.class, f.getSort()));
        }
        if (f.getCriteria() != null) {
            log.info("Filtering " + list.size() + " records");
            log.info("\n" + f.getCriteria().toString());
            try {
                list = filter.filter(f.getCriteria(), list);
            } catch (SmartException e) {
                log.error("Could not filter", e);
            }
        } else {
            log.info("No filtering");
        }

        return f.limitList(list);
    }

    public DataTransferObject updateData(SmartPortletFactory f) {
        TownDto record = (TownDto)f.getSingleDto();
        log.info("Doing update on " + record);
        if (record == null) {
            return null;
        }
        Integer recordId = record.getId();
        if (recordId == null) {
            return null;
        }
        for (int i = 0; i < list.size (); i++) {
            if (recordId.equals(list.get(i).getId())) {
                list.set(i, record);
                return record;
            }
        }
        return null;
    }

    public DataTransferObject addData(SmartPortletFactory f) {
        TownDto record = (TownDto)f.getSingleDto();
        log.info("Doing add");
        if (record == null) {
            return null;
        }
        record.setId(id++);
        list.add(record);
        return record;
    }

    public DataTransferObject removeData(SmartPortletFactory f) {
        TownDto record = (TownDto)f.getSingleDto();
        log.info("Doing remove");
        if (record == null) {
            return null;
        }
        Integer recordId = record.getId ();
        if (recordId == null) {
            return null;
        }
        for (int i = 0; i < list.size (); i++) {
            if (recordId.equals(list.get(i).getId())) {
                list.remove(i);
                return record;
            }
        }
        return null;
    }

    static int id;
    static {
        id = 1;
        list = new ArrayList<TownDto>();
        list.add(new TownDto(id++, "Aberdeen", new Date()));
        list.add(new TownDto(id++, "Adelaide", new Date()));
        list.add(new TownDto(id++, "Albert Falls", new Date()));
        list.add(new TownDto(id++, "Albertinia", new Date()));
        list.add(new TownDto(id++, "Alberton", new Date()));
        list.add(new TownDto(id++, "Alexander Bay", new Date()));
        list.add(new TownDto(id++, "Alexandria, Eastern Cape", new Date()));
        list.add(new TownDto(id++, "Alexandra (Township north of Johannesburg), Gauteng", new Date()));
        list.add(new TownDto(id++, "Alice (Edikeni in Xhosa), Eastern Cape, South Africa", new Date()));
        list.add(new TownDto(id++, "Aliwal North", new Date()));
        list.add(new TownDto(id++, "Allanridge", new Date()));
        list.add(new TownDto(id++, "Alldays", new Date()));
        list.add(new TownDto(id++, "Amanzimtoti", new Date()));
        list.add(new TownDto(id++, "Amersfoort", new Date()));
        list.add(new TownDto(id++, "Amsterdam", new Date()));
        list.add(new TownDto(id++, "Anerley", new Date()));
        list.add(new TownDto(id++, "Arlington", new Date()));
        list.add(new TownDto(id++, "Arniston", new Date()));
        list.add(new TownDto(id++, "Ashton", new Date()));
        list.add(new TownDto(id++, "Askham", new Date()));
        list.add(new TownDto(id++, "Aurora", new Date()));
        list.add(new TownDto(id++, "Babanango", new Date()));
        list.add(new TownDto(id++, "Badplaas", new Date()));
        list.add(new TownDto(id++, "Balfour", new Date()));
        list.add(new TownDto(id++, "Balgowan", new Date()));
        list.add(new TownDto(id++, "Ballito", new Date()));
        list.add(new TownDto(id++, "Bandelierkop", new Date()));
        list.add(new TownDto(id++, "Barberton", new Date()));
        list.add(new TownDto(id++, "Barkly East", new Date()));
        list.add(new TownDto(id++, "Bathurst", new Date()));
        list.add(new TownDto(id++, "Baviaanskloof", new Date()));
        list.add(new TownDto(id++, "Bedford", new Date()));
        list.add(new TownDto(id++, "Bela Bela (previously Warmbaths)", new Date()));
        list.add(new TownDto(id++, "Belfast", new Date()));
        list.add(new TownDto(id++, "Benoni, Gauteng", new Date()));
        list.add(new TownDto(id++, "Bergville", new Date()));
        list.add(new TownDto(id++, "Bethal", new Date()));
        list.add(new TownDto(id++, "Bethlehem", new Date()));
        list.add(new TownDto(id++, "Bethulie", new Date()));
        list.add(new TownDto(id++, "Bhisho (previously Bisho; capital of Eastern Cape)", new Date()));
        list.add(new TownDto(id++, "Bloemhof", new Date()));
        list.add(new TownDto(id++, "Boksburg", new Date()));
        list.add(new TownDto(id++, "Bonza Bay", new Date()));
        list.add(new TownDto(id++, "Bosbokrand", new Date()));
        list.add(new TownDto(id++, "Boshof", new Date()));
        list.add(new TownDto(id++, "Boston", new Date()));
        list.add(new TownDto(id++, "Bothaville", new Date()));
        list.add(new TownDto(id++, "Botshabelo", new Date()));
        list.add(new TownDto(id++, "Brakpan", new Date()));
        list.add(new TownDto(id++, "Brandfort", new Date()));
        list.add(new TownDto(id++, "Bredasdorp", new Date()));
        list.add(new TownDto(id++, "Breyten", new Date()));
        list.add(new TownDto(id++, "Brits", new Date()));
        list.add(new TownDto(id++, "Britstown", new Date()));
        list.add(new TownDto(id++, "Bronkhorstspruit", new Date()));
        list.add(new TownDto(id++, "Bultfontein", new Date()));
        list.add(new TownDto(id++, "Bulwer", new Date()));
        list.add(new TownDto(id++, "Burgersdorp", new Date()));
        list.add(new TownDto(id++, "Butterworth (now Gcuwa)", new Date()));
        list.add(new TownDto(id++, "Byrne", new Date()));
        list.add(new TownDto(id++, "Caledon", new Date()));
        list.add(new TownDto(id++, "Calitzdorp", new Date()));
        list.add(new TownDto(id++, "Calvinia", new Date()));
        list.add(new TownDto(id++, "Carletonville", new Date()));
        list.add(new TownDto(id++, "Carnarvon", new Date()));
        list.add(new TownDto(id++, "Carolina", new Date()));
        list.add(new TownDto(id++, "Cathcart", new Date()));
        list.add(new TownDto(id++, "Catoridge", new Date()));
        list.add(new TownDto(id++, "Cedarville", new Date()));
        list.add(new TownDto(id++, "Centurion", new Date()));
        list.add(new TownDto(id++, "Ceres", new Date()));
        list.add(new TownDto(id++, "Charlestown", new Date()));
        list.add(new TownDto(id++, "Chrissiesmeer", new Date()));
        list.add(new TownDto(id++, "Clanwilliam", new Date()));
        list.add(new TownDto(id++, "Clarens", new Date()));
        list.add(new TownDto(id++, "Clocolan", new Date()));
        list.add(new TownDto(id++, "Colenso", new Date()));
        list.add(new TownDto(id++, "Cornelia", new Date()));
        list.add(new TownDto(id++, "Cookhouse, Eastern Cape", new Date()));
        list.add(new TownDto(id++, "Cradock", new Date()));
        list.add(new TownDto(id++, "Cullinan", new Date()));
        list.add(new TownDto(id++, "Dannhauser", new Date()));
        list.add(new TownDto(id++, "Dargle", new Date()));
        list.add(new TownDto(id++, "Daveyton", new Date()));
        list.add(new TownDto(id++, "De Aar", new Date()));
        list.add(new TownDto(id++, "Dealesville", new Date()));
        list.add(new TownDto(id++, "Delmas", new Date()));
        list.add(new TownDto(id++, "Deneysville", new Date()));
        list.add(new TownDto(id++, "Despatch", new Date()));
        list.add(new TownDto(id++, "Dewetsdorp", new Date()));
        list.add(new TownDto(id++, "Doonside", new Date()));
        list.add(new TownDto(id++, "Dordrecht, Eastern Cape", new Date()));
        list.add(new TownDto(id++, "Drummond", new Date()));
        list.add(new TownDto(id++, "Duduza", new Date()));
        list.add(new TownDto(id++, "Dullstroom", new Date()));
        list.add(new TownDto(id++, "Dundee", new Date()));
        list.add(new TownDto(id++, "Durban", new Date()));
        list.add(new TownDto(id++, "Edenburg", new Date()));
        list.add(new TownDto(id++, "Edenvale", new Date()));
        list.add(new TownDto(id++, "Edenville", new Date()));
        list.add(new TownDto(id++, "ekuPhakameni", new Date()));
        list.add(new TownDto(id++, "Elandslaagte", new Date()));
        list.add(new TownDto(id++, "Elliot, Eastern Cape", new Date()));
        list.add(new TownDto(id++, "Empangeni", new Date()));
        list.add(new TownDto(id++, "Ermelo", new Date()));
        list.add(new TownDto(id++, "Eshowe", new Date()));
        list.add(new TownDto(id++, "Estcourt", new Date()));
        list.add(new TownDto(id++, "Evaton", new Date()));
        list.add(new TownDto(id++, "Excelsior", new Date()));
        list.add(new TownDto(id++, "Fauresmith", new Date()));
        list.add(new TownDto(id++, "Ficksburg", new Date()));
        list.add(new TownDto(id++, "Fisherhaven", new Date()));
        list.add(new TownDto(id++, "Fort Beaufort", new Date()));
        list.add(new TownDto(id++, "Fouriesburg", new Date()));
        list.add(new TownDto(id++, "Frankfort", new Date()));
        list.add(new TownDto(id++, "Franklin", new Date()));
        list.add(new TownDto(id++, "Gcuwa (previously Butterworth)", new Date()));
        list.add(new TownDto(id++, "George", new Date()));
        list.add(new TownDto(id++, "Germiston", new Date()));
        list.add(new TownDto(id++, "Giyani", new Date()));
        list.add(new TownDto(id++, "Glencoe", new Date()));
        list.add(new TownDto(id++, "Graaff Reinet", new Date()));
        list.add(new TownDto(id++, "Grahamstown", new Date()));
        list.add(new TownDto(id++, "Graskop", new Date()));
        list.add(new TownDto(id++, "Greylingstad", new Date()));
        list.add(new TownDto(id++, "Greytown", new Date()));
        list.add(new TownDto(id++, "Groblersdal", new Date()));
        list.add(new TownDto(id++, "Haenertsburg", new Date()));
        list.add(new TownDto(id++, "Hammanskraal", new Date()));
        list.add(new TownDto(id++, "Hankey", new Date()));
        list.add(new TownDto(id++, "Harrismith", new Date()));
        list.add(new TownDto(id++, "Hartbeespoort", new Date()));
        list.add(new TownDto(id++, "Hattingspruit", new Date()));
        list.add(new TownDto(id++, "Hazyview", new Date()));
        list.add(new TownDto(id++, "Hectorspruit", new Date()));
        list.add(new TownDto(id++, "Heidelberg, Gauteng", new Date()));
        list.add(new TownDto(id++, "Heidelberg, Western Cape", new Date()));
        list.add(new TownDto(id++, "Heilbron", new Date()));
        list.add(new TownDto(id++, "Henley on Klip", new Date()));
        list.add(new TownDto(id++, "Hennenman", new Date()));
        list.add(new TownDto(id++, "Hermanus", new Date()));
        list.add(new TownDto(id++, "Hertzogville", new Date()));
        list.add(new TownDto(id++, "Hibberdene", new Date()));
        list.add(new TownDto(id++, "Hillcrest", new Date()));
        list.add(new TownDto(id++, "Hilton", new Date()));
        list.add(new TownDto(id++, "Himeville", new Date()));
        list.add(new TownDto(id++, "Hobhouse", new Date()));
        list.add(new TownDto(id++, "Hoedspruit", new Date()));
        list.add(new TownDto(id++, "Hofmeyr", new Date()));
        list.add(new TownDto(id++, "Hoopstad", new Date()));
        list.add(new TownDto(id++, "Howick", new Date()));
        list.add(new TownDto(id++, "Humansdorp", new Date()));
        list.add(new TownDto(id++, "Idutywa", new Date()));
        list.add(new TownDto(id++, "Ifafa Beach", new Date()));
        list.add(new TownDto(id++, "Illovo Beach", new Date()));
        list.add(new TownDto(id++, "Impendile", new Date()));
        list.add(new TownDto(id++, "Inanda", new Date()));
        list.add(new TownDto(id++, "Ingwavuma", new Date()));
        list.add(new TownDto(id++, "Irene", new Date()));
        list.add(new TownDto(id++, "Isando", new Date()));
        list.add(new TownDto(id++, "Isipingo Beach", new Date()));
        list.add(new TownDto(id++, "Itumeleng", new Date()));
        list.add(new TownDto(id++, "Ixopo", new Date()));
        list.add(new TownDto(id++, "Jansenville", new Date()));
        list.add(new TownDto(id++, "Jacobsdal", new Date()));
        list.add(new TownDto(id++, "Jagersfontein", new Date()));
        list.add(new TownDto(id++, "Jeffreys Bay", new Date()));
        list.add(new TownDto(id++, "Kaapmuiden", new Date()));
        list.add(new TownDto(id++, "Karridene", new Date()));
        list.add(new TownDto(id++, "Katlehong", new Date()));
        list.add(new TownDto(id++, "Kempton Park", new Date()));
        list.add(new TownDto(id++, "Kenton-on-Sea", new Date()));
        list.add(new TownDto(id++, "Kestell", new Date()));
        list.add(new TownDto(id++, "Keurboomstrand", new Date()));
        list.add(new TownDto(id++, "Kgotsong", new Date()));
        list.add(new TownDto(id++, "Khayelitsha", new Date()));
        list.add(new TownDto(id++, "Kimberley", new Date()));
        list.add(new TownDto(id++, "Kingsburgh", new Date()));
        list.add(new TownDto(id++, "King William's Town", new Date()));
        list.add(new TownDto(id++, "Kinross", new Date()));
        list.add(new TownDto(id++, "Kirkwood", new Date()));
        list.add(new TownDto(id++, "Klerksdorp", new Date()));
        list.add(new TownDto(id++, "Kloof", new Date()));
        list.add(new TownDto(id++, "Knysna", new Date()));
        list.add(new TownDto(id++, "Koffiefontein", new Date()));
        list.add(new TownDto(id++, "Kokstad", new Date()));
        list.add(new TownDto(id++, "Komatipoort", new Date()));
        list.add(new TownDto(id++, "Koppies", new Date()));
        list.add(new TownDto(id++, "Kromdraai", new Date()));
        list.add(new TownDto(id++, "Kroonstad", new Date()));
        list.add(new TownDto(id++, "Krugersdorp", new Date()));
        list.add(new TownDto(id++, "KwaDukuza - (previously Stanger)", new Date()));
        list.add(new TownDto(id++, "KwaMashu", new Date()));
        list.add(new TownDto(id++, "KwaMhlanga", new Date()));
        list.add(new TownDto(id++, "KwaThema", new Date()));
        list.add(new TownDto(id++, "Ladybrand", new Date()));
        list.add(new TownDto(id++, "Ladysmith", new Date()));
        list.add(new TownDto(id++, "La Lucia", new Date()));
        list.add(new TownDto(id++, "La Mercy", new Date()));
        list.add(new TownDto(id++, "Lenasia", new Date()));
        list.add(new TownDto(id++, "Lephalale (previously Ellisras)", new Date()));
        list.add(new TownDto(id++, "Lichtenburg", new Date()));
        list.add(new TownDto(id++, "Lindley", new Date()));
        list.add(new TownDto(id++, "Littleton", new Date()));
        list.add(new TownDto(id++, "Loopspruit", new Date()));
        list.add(new TownDto(id++, "Louis Trichardt (From June 2003 to March 2007 known as Makhado)", new Date()));
        list.add(new TownDto(id++, "Louwsburg", new Date()));
        list.add(new TownDto(id++, "Luckhoff", new Date()));
        list.add(new TownDto(id++, "Lydenburg", new Date()));
        list.add(new TownDto(id++, "Machadodorp", new Date()));
        list.add(new TownDto(id++, "Maclear, Eastern Cape", new Date()));
        list.add(new TownDto(id++, "Madadeni", new Date()));
        list.add(new TownDto(id++, "Mafikeng", new Date()));
        list.add(new TownDto(id++, "Magaliesburg", new Date()));
        list.add(new TownDto(id++, "Mahlabatini", new Date()));
        list.add(new TownDto(id++, "Makeleketla", new Date()));
        list.add(new TownDto(id++, "Malelane", new Date()));
        list.add(new TownDto(id++, "Mamelodi", new Date()));
        list.add(new TownDto(id++, "Mandini", new Date()));
        list.add(new TownDto(id++, "Marble Hall", new Date()));
        list.add(new TownDto(id++, "Margate", new Date()));
        list.add(new TownDto(id++, "Marquard", new Date()));
        list.add(new TownDto(id++, "Matatiele", new Date()));
        list.add(new TownDto(id++, "Melmoth", new Date()));
        list.add(new TownDto(id++, "Memel", new Date()));
        list.add(new TownDto(id++, "Merrivale", new Date()));
        list.add(new TownDto(id++, "Meyerton", new Date()));
        list.add(new TownDto(id++, "Middelburg, Eastern Cape", new Date()));
        list.add(new TownDto(id++, "Middelburg, Mpumalanga", new Date()));
        list.add(new TownDto(id++, "Midrand", new Date()));
        list.add(new TownDto(id++, "Mkuze", new Date()));
        list.add(new TownDto(id++, "Mmabatho", new Date()));
        list.add(new TownDto(id++, "Modder River", new Date()));
        list.add(new TownDto(id++, "Modimolle (previously Nylstroom)", new Date()));
        list.add(new TownDto(id++, "Mokopane (previously Potgietersrus)", new Date()));
        list.add(new TownDto(id++, "Molteno, Eastern Cape", new Date()));
        list.add(new TownDto(id++, "Mooirivier", new Date()));
        list.add(new TownDto(id++, "Morgenzon", new Date()));
        list.add(new TownDto(id++, "Mount Edgecombe", new Date()));
        list.add(new TownDto(id++, "Mount Fletcher", new Date()));
        list.add(new TownDto(id++, "Mossel Bay", new Date()));
        list.add(new TownDto(id++, "Mthatha", new Date()));
        list.add(new TownDto(id++, "Mtubatuba", new Date()));
        list.add(new TownDto(id++, "Mtunzini", new Date()));
        list.add(new TownDto(id++, "Muden", new Date()));
        list.add(new TownDto(id++, "Muldersdrift", new Date()));
        list.add(new TownDto(id++, "Musina (previously Messina)", new Date()));
        list.add(new TownDto(id++, "Naboomspruit", new Date()));
        list.add(new TownDto(id++, "Nelspruit", new Date()));
        list.add(new TownDto(id++, "Newcastle", new Date()));
        list.add(new TownDto(id++, "New Germany", new Date()));
        list.add(new TownDto(id++, "New Hanover", new Date()));
        list.add(new TownDto(id++, "Ngcobo", new Date()));
        list.add(new TownDto(id++, "Nieu-Bethesda", new Date()));
        list.add(new TownDto(id++, "Nigel", new Date()));
        list.add(new TownDto(id++, "Nongoma", new Date()));
        list.add(new TownDto(id++, "Nottingham Road", new Date()));
        list.add(new TownDto(id++, "Odendaalsrus", new Date()));
        list.add(new TownDto(id++, "Ogies", new Date()));
        list.add(new TownDto(id++, "Ohrigstad", new Date()));
        list.add(new TownDto(id++, "Orania, Northern Cape", new Date()));
        list.add(new TownDto(id++, "Oranjeville", new Date()));
        list.add(new TownDto(id++, "Orkney", new Date()));
        list.add(new TownDto(id++, "Oyster Bay", new Date()));
        list.add(new TownDto(id++, "Paarl", new Date()));
        list.add(new TownDto(id++, "Palm Beach", new Date()));
        list.add(new TownDto(id++, "Park Rynie", new Date()));
        list.add(new TownDto(id++, "Parys", new Date()));
        list.add(new TownDto(id++, "Patterson, Eastern Cape", new Date()));
        list.add(new TownDto(id++, "Paulpietersburg", new Date()));
        list.add(new TownDto(id++, "Paul Roux", new Date()));
        list.add(new TownDto(id++, "Pennington", new Date()));
        list.add(new TownDto(id++, "Perdekop", new Date()));
        list.add(new TownDto(id++, "Petrusburg", new Date()));
        list.add(new TownDto(id++, "Petrus Steyn", new Date()));
        list.add(new TownDto(id++, "Philippolis", new Date()));
        list.add(new TownDto(id++, "Phuthaditjhaba", new Date()));
        list.add(new TownDto(id++, "Piet Retief", new Date()));
        list.add(new TownDto(id++, "Pilgrim's Rest", new Date()));
        list.add(new TownDto(id++, "Pinetown", new Date()));
        list.add(new TownDto(id++, "Plettenberg Bay", new Date()));
        list.add(new TownDto(id++, "Polokwane (previously Pietersburg)", new Date()));
        list.add(new TownDto(id++, "Pomeroy", new Date()));
        list.add(new TownDto(id++, "Pongola", new Date()));
        list.add(new TownDto(id++, "Port Alfred", new Date()));
        list.add(new TownDto(id++, "Port Edward", new Date()));
        list.add(new TownDto(id++, "Port Shepstone", new Date()));
        list.add(new TownDto(id++, "Port St. Johns", new Date()));
        list.add(new TownDto(id++, "Prieska", new Date()));
        list.add(new TownDto(id++, "Queensburgh", new Date()));
        list.add(new TownDto(id++, "Queenstown", new Date()));
        list.add(new TownDto(id++, "Ramsgate", new Date()));
        list.add(new TownDto(id++, "Randburg", new Date()));
        list.add(new TownDto(id++, "Randfontein", new Date()));
        list.add(new TownDto(id++, "Ratanda", new Date()));
        list.add(new TownDto(id++, "Reddersburg", new Date()));
        list.add(new TownDto(id++, "Reitz", new Date()));
        list.add(new TownDto(id++, "Richards Bay", new Date()));
        list.add(new TownDto(id++, "Richmond", new Date()));
        list.add(new TownDto(id++, "Roodepoort", new Date()));
        list.add(new TownDto(id++, "Rooihuiskraal", new Date()));
        list.add(new TownDto(id++, "Rosendal", new Date()));
        list.add(new TownDto(id++, "Rouxville", new Date()));
        list.add(new TownDto(id++, "Rustenburg", new Date()));
        list.add(new TownDto(id++, "Sabie", new Date()));
        list.add(new TownDto(id++, "Salt Rock", new Date()));
        list.add(new TownDto(id++, "Sandton", new Date()));
        list.add(new TownDto(id++, "Sasolburg", new Date()));
        list.add(new TownDto(id++, "Schweizer-Reneke", new Date()));
        list.add(new TownDto(id++, "Scottburgh", new Date()));
        list.add(new TownDto(id++, "Sebokeng", new Date()));
        list.add(new TownDto(id++, "Secunda", new Date()));
        list.add(new TownDto(id++, "Senekal", new Date()));
        list.add(new TownDto(id++, "Sezela", new Date()));
        list.add(new TownDto(id++, "Sharpeville", new Date()));
        list.add(new TownDto(id++, "Shelly Beach", new Date()));
        list.add(new TownDto(id++, "Smithfield", new Date()));
        list.add(new TownDto(id++, "Somerset East", new Date()));
        list.add(new TownDto(id++, "Somerset West", new Date()));
        list.add(new TownDto(id++, "Southbroom", new Date()));
        list.add(new TownDto(id++, "Soweto", new Date()));
        list.add(new TownDto(id++, "Springbok", new Date()));
        list.add(new TownDto(id++, "Springfontein", new Date()));
        list.add(new TownDto(id++, "Springs", new Date()));
        list.add(new TownDto(id++, "Standerton", new Date()));
        list.add(new TownDto(id++, "Stellenbosch", new Date()));
        list.add(new TownDto(id++, "Steynsrus", new Date()));
        list.add(new TownDto(id++, "St Francis Bay", new Date()));
        list.add(new TownDto(id++, "St Lucia", new Date()));
        list.add(new TownDto(id++, "St Michael's-on-Sea", new Date()));
        list.add(new TownDto(id++, "Strand", new Date()));
        list.add(new TownDto(id++, "Stutterheim", new Date()));
        list.add(new TownDto(id++, "Swartberg", new Date()));
        list.add(new TownDto(id++, "Swellendam", new Date()));
        list.add(new TownDto(id++, "Swinburne", new Date()));
        list.add(new TownDto(id++, "Tarkastad", new Date()));
        list.add(new TownDto(id++, "Tembisa", new Date()));
        list.add(new TownDto(id++, "Thaba Nchu", new Date()));
        list.add(new TownDto(id++, "Thabazimbi", new Date()));
        list.add(new TownDto(id++, "Theunissen", new Date()));
        list.add(new TownDto(id++, "Thohoyandou", new Date()));
        list.add(new TownDto(id++, "Thokoza", new Date()));
        list.add(new TownDto(id++, "Tongaat", new Date()));
        list.add(new TownDto(id++, "Trichardt", new Date()));
        list.add(new TownDto(id++, "Trompsburg", new Date()));
        list.add(new TownDto(id++, "Tsakane", new Date()));
        list.add(new TownDto(id++, "Tugela Ferry", new Date()));
        list.add(new TownDto(id++, "Tulbagh", new Date()));
        list.add(new TownDto(id++, "Tweeling", new Date()));
        list.add(new TownDto(id++, "Tweespruit", new Date()));
        list.add(new TownDto(id++, "Ubombo", new Date()));
        list.add(new TownDto(id++, "Uitenhage", new Date()));
        list.add(new TownDto(id++, "Ulundi", new Date()));
        list.add(new TownDto(id++, "Umbogintwini", new Date()));
        list.add(new TownDto(id++, "Umdloti", new Date()));
        list.add(new TownDto(id++, "Umgababa", new Date()));
        list.add(new TownDto(id++, "Umhlanga Rocks", new Date()));
        list.add(new TownDto(id++, "Umkomaas", new Date()));
        list.add(new TownDto(id++, "Umlazi", new Date()));
        list.add(new TownDto(id++, "Umtentweni", new Date()));
        list.add(new TownDto(id++, "uMthatha (previously Umtata, once served as Capital of Transkei)", new Date()));
        list.add(new TownDto(id++, "Umzinto", new Date()));
        list.add(new TownDto(id++, "Umzumbe", new Date()));
        list.add(new TownDto(id++, "Underberg", new Date()));
        list.add(new TownDto(id++, "Upington", new Date()));
        list.add(new TownDto(id++, "Uniondale, Western Cape", new Date()));
        list.add(new TownDto(id++, "Utrecht", new Date()));
        list.add(new TownDto(id++, "Uvongo", new Date()));
        list.add(new TownDto(id++, "Vaalbank", new Date()));
        list.add(new TownDto(id++, "Vaalwater", new Date()));
        list.add(new TownDto(id++, "Vanderbijlpark", new Date()));
        list.add(new TownDto(id++, "Van Reenen", new Date()));
        list.add(new TownDto(id++, "Van Stadensrus", new Date()));
        list.add(new TownDto(id++, "Ventersburg", new Date()));
        list.add(new TownDto(id++, "Vereeniging", new Date()));
        list.add(new TownDto(id++, "Verkeerdevlei", new Date()));
        list.add(new TownDto(id++, "Verulam", new Date()));
        list.add(new TownDto(id++, "Viljoenskroon", new Date()));
        list.add(new TownDto(id++, "Villiers", new Date()));
        list.add(new TownDto(id++, "Virginia", new Date()));
        list.add(new TownDto(id++, "Vivo", new Date()));
        list.add(new TownDto(id++, "Volksrust", new Date()));
        list.add(new TownDto(id++, "Vosloorus", new Date()));
        list.add(new TownDto(id++, "Vrede", new Date()));
        list.add(new TownDto(id++, "Vredefort", new Date()));
        list.add(new TownDto(id++, "Vryburg", new Date()));
        list.add(new TownDto(id++, "Vryheid", new Date()));
        list.add(new TownDto(id++, "Wakkerstroom", new Date()));
        list.add(new TownDto(id++, "Warden", new Date()));
        list.add(new TownDto(id++, "Warner Beach", new Date()));
        list.add(new TownDto(id++, "Wartburg", new Date()));
        list.add(new TownDto(id++, "Wasbank", new Date()));
        list.add(new TownDto(id++, "Waterval Boven", new Date()));
        list.add(new TownDto(id++, "Waterval Onder", new Date()));
        list.add(new TownDto(id++, "Weenen", new Date()));
        list.add(new TownDto(id++, "Welkom", new Date()));
        list.add(new TownDto(id++, "Wellington", new Date()));
        list.add(new TownDto(id++, "Wepener", new Date()));
        list.add(new TownDto(id++, "Wesselsbron", new Date()));
        list.add(new TownDto(id++, "Westonaria", new Date()));
        list.add(new TownDto(id++, "Westville", new Date()));
        list.add(new TownDto(id++, "White River", new Date()));
        list.add(new TownDto(id++, "Widenham", new Date()));
        list.add(new TownDto(id++, "Wilderness", new Date()));
        list.add(new TownDto(id++, "Williston, Eastern Cape", new Date()));
        list.add(new TownDto(id++, "Winburg", new Date()));
        list.add(new TownDto(id++, "Winkelspruit", new Date()));
        list.add(new TownDto(id++, "Winterton", new Date()));
        list.add(new TownDto(id++, "Witbank", new Date()));
        list.add(new TownDto(id++, "Worcester", new Date()));
        list.add(new TownDto(id++, "York", new Date()));
        list.add(new TownDto(id++, "Zastron", new Date()));
        list.add(new TownDto(id++, "Zeerust", new Date()));
        list.add(new TownDto(id++, "Zwelitsha", new Date()));
    }
}
