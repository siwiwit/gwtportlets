/*
 * GWT Portlets Framework (http://code.google.com/p/gwtportlets/)
 * Copyright 2009 Business Systems Group (Africa)
 *
 * This file is part of GWT Portlets.
 *
 * GWT Portlets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GWT Portlets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GWT Portlets.  If not, see <http://www.gnu.org/licenses/>.
 */

package smartgwt.server;

import org.apache.log4j.Logger;
import org.gwtportlets.portlet.server.smartgwt.SmartWidgetDataProvider;
import smartgwt.client.data.TownRecord;
import smartgwt.client.ui.CompoundEditorPortlet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 */
public class CompoundEditorDataProvider extends SmartWidgetDataProvider<CompoundEditorPortlet.Factory> {

    private static final Logger log = Logger.getLogger(CompoundEditorDataProvider.class);

    public Class getWidgetFactoryClass() {
        return CompoundEditorPortlet.Factory.class;
    }

    static List<TownRecord> list;

    public void executeFetch(CompoundEditorPortlet.Factory f) {
        log.info("Doing fetch: " + f.getStartRow() + " -> " + f.getEndRow());
        f.data = new ArrayList<TownRecord>(list.subList(f.getStartRow(), Math.min(f.getEndRow(), list.size())));
        f.setTotalRows(list.size());
    }

    public void executeAdd(CompoundEditorPortlet.Factory f) {
        log.info("Doing add");
        if (f.record != null) {
            f.record.setId (id++);
            list.add (f.record);
        }
    }

    public void executeUpdate(CompoundEditorPortlet.Factory f) {
        log.info("Doing update on " + f.record);
        if (f.record != null) {
            Integer recordId = f.record.getId();
            if (recordId != null) {
                int index = -1;
                for (int i = 0; i < list.size (); i++) {
                    if (recordId.equals (list.get (i).getId ())) {
                        index = i;
                        break;
                    }
                }
                if (index >= 0) {
                    list.set (index, f.record);
                    return;
                }
            }
        }
        f.record = null;
    }

    public void executeRemove(CompoundEditorPortlet.Factory f) {
        log.info("Doing remove");
        if (f.record != null) {
            Integer recordId = f.record.getId ();
            if (recordId != null) {
                int index = -1;
                for (int i = 0; i < list.size (); i++) {
                    if (recordId.equals (list.get (i).getId ())) {
                        index = i;
                        break;
                    }
                }
                if (index >= 0) {
                    list.remove(index);
                    return;
                }
            }
        }
        f.record = null;
    }

    static int id;
    static {
        id = 1;
        list = new ArrayList<TownRecord>();
        list.add(new TownRecord(id++, "Aberdeen", new Date()));
        list.add(new TownRecord(id++, "Adelaide", new Date()));
        list.add(new TownRecord(id++, "Albert Falls", new Date()));
        list.add(new TownRecord(id++, "Albertinia", new Date()));
        list.add(new TownRecord(id++, "Alberton", new Date()));
        list.add(new TownRecord(id++, "Alexander Bay", new Date()));
        list.add(new TownRecord(id++, "Alexandria, Eastern Cape", new Date()));
        list.add(new TownRecord(id++, "Alexandra (Township north of Johannesburg), Gauteng", new Date()));
        list.add(new TownRecord(id++, "Alice (Edikeni in Xhosa), Eastern Cape, South Africa", new Date()));
        list.add(new TownRecord(id++, "Aliwal North", new Date()));
        list.add(new TownRecord(id++, "Allanridge", new Date()));
        list.add(new TownRecord(id++, "Alldays", new Date()));
        list.add(new TownRecord(id++, "Amanzimtoti", new Date()));
        list.add(new TownRecord(id++, "Amersfoort", new Date()));
        list.add(new TownRecord(id++, "Amsterdam", new Date()));
        list.add(new TownRecord(id++, "Anerley", new Date()));
        list.add(new TownRecord(id++, "Arlington", new Date()));
        list.add(new TownRecord(id++, "Arniston", new Date()));
        list.add(new TownRecord(id++, "Ashton", new Date()));
        list.add(new TownRecord(id++, "Askham", new Date()));
        list.add(new TownRecord(id++, "Aurora", new Date()));
        list.add(new TownRecord(id++, "Babanango", new Date()));
        list.add(new TownRecord(id++, "Badplaas", new Date()));
        list.add(new TownRecord(id++, "Balfour", new Date()));
        list.add(new TownRecord(id++, "Balgowan", new Date()));
        list.add(new TownRecord(id++, "Ballito", new Date()));
        list.add(new TownRecord(id++, "Bandelierkop", new Date()));
        list.add(new TownRecord(id++, "Barberton", new Date()));
        list.add(new TownRecord(id++, "Barkly East", new Date()));
        list.add(new TownRecord(id++, "Bathurst", new Date()));
        list.add(new TownRecord(id++, "Baviaanskloof", new Date()));
        list.add(new TownRecord(id++, "Bedford", new Date()));
        list.add(new TownRecord(id++, "Bela Bela (previously Warmbaths)", new Date()));
        list.add(new TownRecord(id++, "Belfast", new Date()));
        list.add(new TownRecord(id++, "Benoni, Gauteng", new Date()));
        list.add(new TownRecord(id++, "Bergville", new Date()));
        list.add(new TownRecord(id++, "Bethal", new Date()));
        list.add(new TownRecord(id++, "Bethlehem", new Date()));
        list.add(new TownRecord(id++, "Bethulie", new Date()));
        list.add(new TownRecord(id++, "Bhisho (previously Bisho; capital of Eastern Cape)", new Date()));
        list.add(new TownRecord(id++, "Bloemhof", new Date()));
        list.add(new TownRecord(id++, "Boksburg", new Date()));
        list.add(new TownRecord(id++, "Bonza Bay", new Date()));
        list.add(new TownRecord(id++, "Bosbokrand", new Date()));
        list.add(new TownRecord(id++, "Boshof", new Date()));
        list.add(new TownRecord(id++, "Boston", new Date()));
        list.add(new TownRecord(id++, "Bothaville", new Date()));
        list.add(new TownRecord(id++, "Botshabelo", new Date()));
        list.add(new TownRecord(id++, "Brakpan", new Date()));
        list.add(new TownRecord(id++, "Brandfort", new Date()));
        list.add(new TownRecord(id++, "Bredasdorp", new Date()));
        list.add(new TownRecord(id++, "Breyten", new Date()));
        list.add(new TownRecord(id++, "Brits", new Date()));
        list.add(new TownRecord(id++, "Britstown", new Date()));
        list.add(new TownRecord(id++, "Bronkhorstspruit", new Date()));
        list.add(new TownRecord(id++, "Bultfontein", new Date()));
        list.add(new TownRecord(id++, "Bulwer", new Date()));
        list.add(new TownRecord(id++, "Burgersdorp", new Date()));
        list.add(new TownRecord(id++, "Butterworth (now Gcuwa)", new Date()));
        list.add(new TownRecord(id++, "Byrne", new Date()));
        list.add(new TownRecord(id++, "Caledon", new Date()));
        list.add(new TownRecord(id++, "Calitzdorp", new Date()));
        list.add(new TownRecord(id++, "Calvinia", new Date()));
        list.add(new TownRecord(id++, "Carletonville", new Date()));
        list.add(new TownRecord(id++, "Carnarvon", new Date()));
        list.add(new TownRecord(id++, "Carolina", new Date()));
        list.add(new TownRecord(id++, "Cathcart", new Date()));
        list.add(new TownRecord(id++, "Catoridge", new Date()));
        list.add(new TownRecord(id++, "Cedarville", new Date()));
        list.add(new TownRecord(id++, "Centurion", new Date()));
        list.add(new TownRecord(id++, "Ceres", new Date()));
        list.add(new TownRecord(id++, "Charlestown", new Date()));
        list.add(new TownRecord(id++, "Chrissiesmeer", new Date()));
        list.add(new TownRecord(id++, "Clanwilliam", new Date()));
        list.add(new TownRecord(id++, "Clarens", new Date()));
        list.add(new TownRecord(id++, "Clocolan", new Date()));
        list.add(new TownRecord(id++, "Colenso", new Date()));
        list.add(new TownRecord(id++, "Cornelia", new Date()));
        list.add(new TownRecord(id++, "Cookhouse, Eastern Cape", new Date()));
        list.add(new TownRecord(id++, "Cradock", new Date()));
        list.add(new TownRecord(id++, "Cullinan", new Date()));
        list.add(new TownRecord(id++, "Dannhauser", new Date()));
        list.add(new TownRecord(id++, "Dargle", new Date()));
        list.add(new TownRecord(id++, "Daveyton", new Date()));
        list.add(new TownRecord(id++, "De Aar", new Date()));
        list.add(new TownRecord(id++, "Dealesville", new Date()));
        list.add(new TownRecord(id++, "Delmas", new Date()));
        list.add(new TownRecord(id++, "Deneysville", new Date()));
        list.add(new TownRecord(id++, "Despatch", new Date()));
        list.add(new TownRecord(id++, "Dewetsdorp", new Date()));
        list.add(new TownRecord(id++, "Doonside", new Date()));
        list.add(new TownRecord(id++, "Dordrecht, Eastern Cape", new Date()));
        list.add(new TownRecord(id++, "Drummond", new Date()));
        list.add(new TownRecord(id++, "Duduza", new Date()));
        list.add(new TownRecord(id++, "Dullstroom", new Date()));
        list.add(new TownRecord(id++, "Dundee", new Date()));
        list.add(new TownRecord(id++, "Durban", new Date()));
        list.add(new TownRecord(id++, "Edenburg", new Date()));
        list.add(new TownRecord(id++, "Edenvale", new Date()));
        list.add(new TownRecord(id++, "Edenville", new Date()));
        list.add(new TownRecord(id++, "ekuPhakameni", new Date()));
        list.add(new TownRecord(id++, "Elandslaagte", new Date()));
        list.add(new TownRecord(id++, "Elliot, Eastern Cape", new Date()));
        list.add(new TownRecord(id++, "Empangeni", new Date()));
        list.add(new TownRecord(id++, "Ermelo", new Date()));
        list.add(new TownRecord(id++, "Eshowe", new Date()));
        list.add(new TownRecord(id++, "Estcourt", new Date()));
        list.add(new TownRecord(id++, "Evaton", new Date()));
        list.add(new TownRecord(id++, "Excelsior", new Date()));
        list.add(new TownRecord(id++, "Fauresmith", new Date()));
        list.add(new TownRecord(id++, "Ficksburg", new Date()));
        list.add(new TownRecord(id++, "Fisherhaven", new Date()));
        list.add(new TownRecord(id++, "Fort Beaufort", new Date()));
        list.add(new TownRecord(id++, "Fouriesburg", new Date()));
        list.add(new TownRecord(id++, "Frankfort", new Date()));
        list.add(new TownRecord(id++, "Franklin", new Date()));
        list.add(new TownRecord(id++, "Gcuwa (previously Butterworth)", new Date()));
        list.add(new TownRecord(id++, "George", new Date()));
        list.add(new TownRecord(id++, "Germiston", new Date()));
        list.add(new TownRecord(id++, "Giyani", new Date()));
        list.add(new TownRecord(id++, "Glencoe", new Date()));
        list.add(new TownRecord(id++, "Graaff Reinet", new Date()));
        list.add(new TownRecord(id++, "Grahamstown", new Date()));
        list.add(new TownRecord(id++, "Graskop", new Date()));
        list.add(new TownRecord(id++, "Greylingstad", new Date()));
        list.add(new TownRecord(id++, "Greytown", new Date()));
        list.add(new TownRecord(id++, "Groblersdal", new Date()));
        list.add(new TownRecord(id++, "Haenertsburg", new Date()));
        list.add(new TownRecord(id++, "Hammanskraal", new Date()));
        list.add(new TownRecord(id++, "Hankey", new Date()));
        list.add(new TownRecord(id++, "Harrismith", new Date()));
        list.add(new TownRecord(id++, "Hartbeespoort", new Date()));
        list.add(new TownRecord(id++, "Hattingspruit", new Date()));
        list.add(new TownRecord(id++, "Hazyview", new Date()));
        list.add(new TownRecord(id++, "Hectorspruit", new Date()));
        list.add(new TownRecord(id++, "Heidelberg, Gauteng", new Date()));
        list.add(new TownRecord(id++, "Heidelberg, Western Cape", new Date()));
        list.add(new TownRecord(id++, "Heilbron", new Date()));
        list.add(new TownRecord(id++, "Henley on Klip", new Date()));
        list.add(new TownRecord(id++, "Hennenman", new Date()));
        list.add(new TownRecord(id++, "Hermanus", new Date()));
        list.add(new TownRecord(id++, "Hertzogville", new Date()));
        list.add(new TownRecord(id++, "Hibberdene", new Date()));
        list.add(new TownRecord(id++, "Hillcrest", new Date()));
        list.add(new TownRecord(id++, "Hilton", new Date()));
        list.add(new TownRecord(id++, "Himeville", new Date()));
        list.add(new TownRecord(id++, "Hobhouse", new Date()));
        list.add(new TownRecord(id++, "Hoedspruit", new Date()));
        list.add(new TownRecord(id++, "Hofmeyr", new Date()));
        list.add(new TownRecord(id++, "Hoopstad", new Date()));
        list.add(new TownRecord(id++, "Howick", new Date()));
        list.add(new TownRecord(id++, "Humansdorp", new Date()));
        list.add(new TownRecord(id++, "Idutywa", new Date()));
        list.add(new TownRecord(id++, "Ifafa Beach", new Date()));
        list.add(new TownRecord(id++, "Illovo Beach", new Date()));
        list.add(new TownRecord(id++, "Impendile", new Date()));
        list.add(new TownRecord(id++, "Inanda", new Date()));
        list.add(new TownRecord(id++, "Ingwavuma", new Date()));
        list.add(new TownRecord(id++, "Irene", new Date()));
        list.add(new TownRecord(id++, "Isando", new Date()));
        list.add(new TownRecord(id++, "Isipingo Beach", new Date()));
        list.add(new TownRecord(id++, "Itumeleng", new Date()));
        list.add(new TownRecord(id++, "Ixopo", new Date()));
        list.add(new TownRecord(id++, "Jansenville", new Date()));
        list.add(new TownRecord(id++, "Jacobsdal", new Date()));
        list.add(new TownRecord(id++, "Jagersfontein", new Date()));
        list.add(new TownRecord(id++, "Jeffreys Bay", new Date()));
        list.add(new TownRecord(id++, "Kaapmuiden", new Date()));
        list.add(new TownRecord(id++, "Karridene", new Date()));
        list.add(new TownRecord(id++, "Katlehong", new Date()));
        list.add(new TownRecord(id++, "Kempton Park", new Date()));
        list.add(new TownRecord(id++, "Kenton-on-Sea", new Date()));
        list.add(new TownRecord(id++, "Kestell", new Date()));
        list.add(new TownRecord(id++, "Keurboomstrand", new Date()));
        list.add(new TownRecord(id++, "Kgotsong", new Date()));
        list.add(new TownRecord(id++, "Khayelitsha", new Date()));
        list.add(new TownRecord(id++, "Kimberley", new Date()));
        list.add(new TownRecord(id++, "Kingsburgh", new Date()));
        list.add(new TownRecord(id++, "King William's Town", new Date()));
        list.add(new TownRecord(id++, "Kinross", new Date()));
        list.add(new TownRecord(id++, "Kirkwood", new Date()));
        list.add(new TownRecord(id++, "Klerksdorp", new Date()));
        list.add(new TownRecord(id++, "Kloof", new Date()));
        list.add(new TownRecord(id++, "Knysna", new Date()));
        list.add(new TownRecord(id++, "Koffiefontein", new Date()));
        list.add(new TownRecord(id++, "Kokstad", new Date()));
        list.add(new TownRecord(id++, "Komatipoort", new Date()));
        list.add(new TownRecord(id++, "Koppies", new Date()));
        list.add(new TownRecord(id++, "Kromdraai", new Date()));
        list.add(new TownRecord(id++, "Kroonstad", new Date()));
        list.add(new TownRecord(id++, "Krugersdorp", new Date()));
        list.add(new TownRecord(id++, "KwaDukuza - (previously Stanger)", new Date()));
        list.add(new TownRecord(id++, "KwaMashu", new Date()));
        list.add(new TownRecord(id++, "KwaMhlanga", new Date()));
        list.add(new TownRecord(id++, "KwaThema", new Date()));
        list.add(new TownRecord(id++, "Ladybrand", new Date()));
        list.add(new TownRecord(id++, "Ladysmith", new Date()));
        list.add(new TownRecord(id++, "La Lucia", new Date()));
        list.add(new TownRecord(id++, "La Mercy", new Date()));
        list.add(new TownRecord(id++, "Lenasia", new Date()));
        list.add(new TownRecord(id++, "Lephalale (previously Ellisras)", new Date()));
        list.add(new TownRecord(id++, "Lichtenburg", new Date()));
        list.add(new TownRecord(id++, "Lindley", new Date()));
        list.add(new TownRecord(id++, "Littleton", new Date()));
        list.add(new TownRecord(id++, "Loopspruit", new Date()));
        list.add(new TownRecord(id++, "Louis Trichardt (From June 2003 to March 2007 known as Makhado)", new Date()));
        list.add(new TownRecord(id++, "Louwsburg", new Date()));
        list.add(new TownRecord(id++, "Luckhoff", new Date()));
        list.add(new TownRecord(id++, "Lydenburg", new Date()));
        list.add(new TownRecord(id++, "Machadodorp", new Date()));
        list.add(new TownRecord(id++, "Maclear, Eastern Cape", new Date()));
        list.add(new TownRecord(id++, "Madadeni", new Date()));
        list.add(new TownRecord(id++, "Mafikeng", new Date()));
        list.add(new TownRecord(id++, "Magaliesburg", new Date()));
        list.add(new TownRecord(id++, "Mahlabatini", new Date()));
        list.add(new TownRecord(id++, "Makeleketla", new Date()));
        list.add(new TownRecord(id++, "Malelane", new Date()));
        list.add(new TownRecord(id++, "Mamelodi", new Date()));
        list.add(new TownRecord(id++, "Mandini", new Date()));
        list.add(new TownRecord(id++, "Marble Hall", new Date()));
        list.add(new TownRecord(id++, "Margate", new Date()));
        list.add(new TownRecord(id++, "Marquard", new Date()));
        list.add(new TownRecord(id++, "Matatiele", new Date()));
        list.add(new TownRecord(id++, "Melmoth", new Date()));
        list.add(new TownRecord(id++, "Memel", new Date()));
        list.add(new TownRecord(id++, "Merrivale", new Date()));
        list.add(new TownRecord(id++, "Meyerton", new Date()));
        list.add(new TownRecord(id++, "Middelburg, Eastern Cape", new Date()));
        list.add(new TownRecord(id++, "Middelburg, Mpumalanga", new Date()));
        list.add(new TownRecord(id++, "Midrand", new Date()));
        list.add(new TownRecord(id++, "Mkuze", new Date()));
        list.add(new TownRecord(id++, "Mmabatho", new Date()));
        list.add(new TownRecord(id++, "Modder River", new Date()));
        list.add(new TownRecord(id++, "Modimolle (previously Nylstroom)", new Date()));
        list.add(new TownRecord(id++, "Mokopane (previously Potgietersrus)", new Date()));
        list.add(new TownRecord(id++, "Molteno, Eastern Cape", new Date()));
        list.add(new TownRecord(id++, "Mooirivier", new Date()));
        list.add(new TownRecord(id++, "Morgenzon", new Date()));
        list.add(new TownRecord(id++, "Mount Edgecombe", new Date()));
        list.add(new TownRecord(id++, "Mount Fletcher", new Date()));
        list.add(new TownRecord(id++, "Mossel Bay", new Date()));
        list.add(new TownRecord(id++, "Mthatha", new Date()));
        list.add(new TownRecord(id++, "Mtubatuba", new Date()));
        list.add(new TownRecord(id++, "Mtunzini", new Date()));
        list.add(new TownRecord(id++, "Muden", new Date()));
        list.add(new TownRecord(id++, "Muldersdrift", new Date()));
        list.add(new TownRecord(id++, "Musina (previously Messina)", new Date()));
        list.add(new TownRecord(id++, "Naboomspruit", new Date()));
        list.add(new TownRecord(id++, "Nelspruit", new Date()));
        list.add(new TownRecord(id++, "Newcastle", new Date()));
        list.add(new TownRecord(id++, "New Germany", new Date()));
        list.add(new TownRecord(id++, "New Hanover", new Date()));
        list.add(new TownRecord(id++, "Ngcobo", new Date()));
        list.add(new TownRecord(id++, "Nieu-Bethesda", new Date()));
        list.add(new TownRecord(id++, "Nigel", new Date()));
        list.add(new TownRecord(id++, "Nongoma", new Date()));
        list.add(new TownRecord(id++, "Nottingham Road", new Date()));
        list.add(new TownRecord(id++, "Odendaalsrus", new Date()));
        list.add(new TownRecord(id++, "Ogies", new Date()));
        list.add(new TownRecord(id++, "Ohrigstad", new Date()));
        list.add(new TownRecord(id++, "Orania, Northern Cape", new Date()));
        list.add(new TownRecord(id++, "Oranjeville", new Date()));
        list.add(new TownRecord(id++, "Orkney", new Date()));
        list.add(new TownRecord(id++, "Oyster Bay", new Date()));
        list.add(new TownRecord(id++, "Paarl", new Date()));
        list.add(new TownRecord(id++, "Palm Beach", new Date()));
        list.add(new TownRecord(id++, "Park Rynie", new Date()));
        list.add(new TownRecord(id++, "Parys", new Date()));
        list.add(new TownRecord(id++, "Patterson, Eastern Cape", new Date()));
        list.add(new TownRecord(id++, "Paulpietersburg", new Date()));
        list.add(new TownRecord(id++, "Paul Roux", new Date()));
        list.add(new TownRecord(id++, "Pennington", new Date()));
        list.add(new TownRecord(id++, "Perdekop", new Date()));
        list.add(new TownRecord(id++, "Petrusburg", new Date()));
        list.add(new TownRecord(id++, "Petrus Steyn", new Date()));
        list.add(new TownRecord(id++, "Philippolis", new Date()));
        list.add(new TownRecord(id++, "Phuthaditjhaba", new Date()));
        list.add(new TownRecord(id++, "Piet Retief", new Date()));
        list.add(new TownRecord(id++, "Pilgrim's Rest", new Date()));
        list.add(new TownRecord(id++, "Pinetown", new Date()));
        list.add(new TownRecord(id++, "Plettenberg Bay", new Date()));
        list.add(new TownRecord(id++, "Polokwane (previously Pietersburg)", new Date()));
        list.add(new TownRecord(id++, "Pomeroy", new Date()));
        list.add(new TownRecord(id++, "Pongola", new Date()));
        list.add(new TownRecord(id++, "Port Alfred", new Date()));
        list.add(new TownRecord(id++, "Port Edward", new Date()));
        list.add(new TownRecord(id++, "Port Shepstone", new Date()));
        list.add(new TownRecord(id++, "Port St. Johns", new Date()));
        list.add(new TownRecord(id++, "Prieska", new Date()));
        list.add(new TownRecord(id++, "Queensburgh", new Date()));
        list.add(new TownRecord(id++, "Queenstown", new Date()));
        list.add(new TownRecord(id++, "Ramsgate", new Date()));
        list.add(new TownRecord(id++, "Randburg", new Date()));
        list.add(new TownRecord(id++, "Randfontein", new Date()));
        list.add(new TownRecord(id++, "Ratanda", new Date()));
        list.add(new TownRecord(id++, "Reddersburg", new Date()));
        list.add(new TownRecord(id++, "Reitz", new Date()));
        list.add(new TownRecord(id++, "Richards Bay", new Date()));
        list.add(new TownRecord(id++, "Richmond", new Date()));
        list.add(new TownRecord(id++, "Roodepoort", new Date()));
        list.add(new TownRecord(id++, "Rooihuiskraal", new Date()));
        list.add(new TownRecord(id++, "Rosendal", new Date()));
        list.add(new TownRecord(id++, "Rouxville", new Date()));
        list.add(new TownRecord(id++, "Rustenburg", new Date()));
        list.add(new TownRecord(id++, "Sabie", new Date()));
        list.add(new TownRecord(id++, "Salt Rock", new Date()));
        list.add(new TownRecord(id++, "Sandton", new Date()));
        list.add(new TownRecord(id++, "Sasolburg", new Date()));
        list.add(new TownRecord(id++, "Schweizer-Reneke", new Date()));
        list.add(new TownRecord(id++, "Scottburgh", new Date()));
        list.add(new TownRecord(id++, "Sebokeng", new Date()));
        list.add(new TownRecord(id++, "Secunda", new Date()));
        list.add(new TownRecord(id++, "Senekal", new Date()));
        list.add(new TownRecord(id++, "Sezela", new Date()));
        list.add(new TownRecord(id++, "Sharpeville", new Date()));
        list.add(new TownRecord(id++, "Shelly Beach", new Date()));
        list.add(new TownRecord(id++, "Smithfield", new Date()));
        list.add(new TownRecord(id++, "Somerset East", new Date()));
        list.add(new TownRecord(id++, "Somerset West", new Date()));
        list.add(new TownRecord(id++, "Southbroom", new Date()));
        list.add(new TownRecord(id++, "Soweto", new Date()));
        list.add(new TownRecord(id++, "Springbok", new Date()));
        list.add(new TownRecord(id++, "Springfontein", new Date()));
        list.add(new TownRecord(id++, "Springs", new Date()));
        list.add(new TownRecord(id++, "Standerton", new Date()));
        list.add(new TownRecord(id++, "Stellenbosch", new Date()));
        list.add(new TownRecord(id++, "Steynsrus", new Date()));
        list.add(new TownRecord(id++, "St Francis Bay", new Date()));
        list.add(new TownRecord(id++, "St Lucia", new Date()));
        list.add(new TownRecord(id++, "St Michael's-on-Sea", new Date()));
        list.add(new TownRecord(id++, "Strand", new Date()));
        list.add(new TownRecord(id++, "Stutterheim", new Date()));
        list.add(new TownRecord(id++, "Swartberg", new Date()));
        list.add(new TownRecord(id++, "Swellendam", new Date()));
        list.add(new TownRecord(id++, "Swinburne", new Date()));
        list.add(new TownRecord(id++, "Tarkastad", new Date()));
        list.add(new TownRecord(id++, "Tembisa", new Date()));
        list.add(new TownRecord(id++, "Thaba Nchu", new Date()));
        list.add(new TownRecord(id++, "Thabazimbi", new Date()));
        list.add(new TownRecord(id++, "Theunissen", new Date()));
        list.add(new TownRecord(id++, "Thohoyandou", new Date()));
        list.add(new TownRecord(id++, "Thokoza", new Date()));
        list.add(new TownRecord(id++, "Tongaat", new Date()));
        list.add(new TownRecord(id++, "Trichardt", new Date()));
        list.add(new TownRecord(id++, "Trompsburg", new Date()));
        list.add(new TownRecord(id++, "Tsakane", new Date()));
        list.add(new TownRecord(id++, "Tugela Ferry", new Date()));
        list.add(new TownRecord(id++, "Tulbagh", new Date()));
        list.add(new TownRecord(id++, "Tweeling", new Date()));
        list.add(new TownRecord(id++, "Tweespruit", new Date()));
        list.add(new TownRecord(id++, "Ubombo", new Date()));
        list.add(new TownRecord(id++, "Uitenhage", new Date()));
        list.add(new TownRecord(id++, "Ulundi", new Date()));
        list.add(new TownRecord(id++, "Umbogintwini", new Date()));
        list.add(new TownRecord(id++, "Umdloti", new Date()));
        list.add(new TownRecord(id++, "Umgababa", new Date()));
        list.add(new TownRecord(id++, "Umhlanga Rocks", new Date()));
        list.add(new TownRecord(id++, "Umkomaas", new Date()));
        list.add(new TownRecord(id++, "Umlazi", new Date()));
        list.add(new TownRecord(id++, "Umtentweni", new Date()));
        list.add(new TownRecord(id++, "uMthatha (previously Umtata, once served as Capital of Transkei)", new Date()));
        list.add(new TownRecord(id++, "Umzinto", new Date()));
        list.add(new TownRecord(id++, "Umzumbe", new Date()));
        list.add(new TownRecord(id++, "Underberg", new Date()));
        list.add(new TownRecord(id++, "Upington", new Date()));
        list.add(new TownRecord(id++, "Uniondale, Western Cape", new Date()));
        list.add(new TownRecord(id++, "Utrecht", new Date()));
        list.add(new TownRecord(id++, "Uvongo", new Date()));
        list.add(new TownRecord(id++, "Vaalbank", new Date()));
        list.add(new TownRecord(id++, "Vaalwater", new Date()));
        list.add(new TownRecord(id++, "Vanderbijlpark", new Date()));
        list.add(new TownRecord(id++, "Van Reenen", new Date()));
        list.add(new TownRecord(id++, "Van Stadensrus", new Date()));
        list.add(new TownRecord(id++, "Ventersburg", new Date()));
        list.add(new TownRecord(id++, "Vereeniging", new Date()));
        list.add(new TownRecord(id++, "Verkeerdevlei", new Date()));
        list.add(new TownRecord(id++, "Verulam", new Date()));
        list.add(new TownRecord(id++, "Viljoenskroon", new Date()));
        list.add(new TownRecord(id++, "Villiers", new Date()));
        list.add(new TownRecord(id++, "Virginia", new Date()));
        list.add(new TownRecord(id++, "Vivo", new Date()));
        list.add(new TownRecord(id++, "Volksrust", new Date()));
        list.add(new TownRecord(id++, "Vosloorus", new Date()));
        list.add(new TownRecord(id++, "Vrede", new Date()));
        list.add(new TownRecord(id++, "Vredefort", new Date()));
        list.add(new TownRecord(id++, "Vryburg", new Date()));
        list.add(new TownRecord(id++, "Vryheid", new Date()));
        list.add(new TownRecord(id++, "Wakkerstroom", new Date()));
        list.add(new TownRecord(id++, "Warden", new Date()));
        list.add(new TownRecord(id++, "Warner Beach", new Date()));
        list.add(new TownRecord(id++, "Wartburg", new Date()));
        list.add(new TownRecord(id++, "Wasbank", new Date()));
        list.add(new TownRecord(id++, "Waterval Boven", new Date()));
        list.add(new TownRecord(id++, "Waterval Onder", new Date()));
        list.add(new TownRecord(id++, "Weenen", new Date()));
        list.add(new TownRecord(id++, "Welkom", new Date()));
        list.add(new TownRecord(id++, "Wellington", new Date()));
        list.add(new TownRecord(id++, "Wepener", new Date()));
        list.add(new TownRecord(id++, "Wesselsbron", new Date()));
        list.add(new TownRecord(id++, "Westonaria", new Date()));
        list.add(new TownRecord(id++, "Westville", new Date()));
        list.add(new TownRecord(id++, "White River", new Date()));
        list.add(new TownRecord(id++, "Widenham", new Date()));
        list.add(new TownRecord(id++, "Wilderness", new Date()));
        list.add(new TownRecord(id++, "Williston, Eastern Cape", new Date()));
        list.add(new TownRecord(id++, "Winburg", new Date()));
        list.add(new TownRecord(id++, "Winkelspruit", new Date()));
        list.add(new TownRecord(id++, "Winterton", new Date()));
        list.add(new TownRecord(id++, "Witbank", new Date()));
        list.add(new TownRecord(id++, "Worcester", new Date()));
        list.add(new TownRecord(id++, "York", new Date()));
        list.add(new TownRecord(id++, "Zastron", new Date()));
        list.add(new TownRecord(id++, "Zeerust", new Date()));
        list.add(new TownRecord(id++, "Zwelitsha", new Date()));
    }
}