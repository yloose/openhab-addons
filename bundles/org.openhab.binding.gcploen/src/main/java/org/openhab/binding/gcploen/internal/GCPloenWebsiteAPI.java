/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.binding.gcploen.internal;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link GCPloenWebsiteAPI} is responsible for providing communication with
 * the web service.
 *
 * @author Yan Loose - Initial contribution
 */
@NonNullByDefault
public class GCPloenWebsiteAPI {

    private final String district;
    private final String street;

    private static final String BASEURL = "https://www.kreis-ploen.de/output/abfall_export.php";

    private final Logger logger = LoggerFactory.getLogger(GCPloenWebsiteAPI.class);

    public enum GarbageTypes {
        abfallart_1_1("Restabfall 14-täglich"),
        abfallart_1_2("Restabfall 4-wöchentlich"),
        abfallart_1_3("Gelber Sack"),
        abfallart_1_4("Bioabfall 2-wöchentlich"),
        abfallart_1_5("Papier 4-wöchentlich"),
        abfallart_1_6("Papiercontainer 4-wöchentlich"),
        abfallart_1_7("Restabfallcontainer 14-täglich");

        public final String label;

        private GarbageTypes(String label) {
            this.label = label;
        }
    }

    public GCPloenWebsiteAPI(String district, String street) {
        this.district = district;
        this.street = street;
    }

    private String buildURL() {
        return BASEURL + "?call=suche&print=1&ort=" + this.district + "&strasse=" + this.street;
    }

    public boolean checkAvailability() {
        String url = buildURL();
        Connection.Response response;
        try {
            response = Jsoup.connect(url).timeout(10000).execute();
        } catch (IOException e) {
            throw new GCPloenRuntimeException(e, GCPloenRuntimeException.ErrorCode.API_NETWORK,
                    "Could not interface with network.");
        }

        int statusCode = response.statusCode();

        if (statusCode == 200) {
            Document doc;
            try {
                doc = response.parse();
            } catch (IOException e) {
                throw new GCPloenRuntimeException(e, GCPloenRuntimeException.ErrorCode.API_NETWORK,
                        "Could not parse server response.");
            }

            Elements tables = doc.getElementsByTag("table");

            if (tables.toArray().length > 1) {
                logger.debug("Received valid response from server.");
                return true;
            } else {
                throw new GCPloenRuntimeException(GCPloenRuntimeException.ErrorCode.API_CONFIG,
                        "Could not receive valid data from server with given config.");
            }
        } else {
            throw new GCPloenRuntimeException(GCPloenRuntimeException.ErrorCode.API_NETWORK,
                    "Received invalid response from server. CODE=" + response.statusCode());
        }
    }

    public Map<GarbageTypes, List<LocalDate>> getData() {
        Document doc;
        try {
            doc = Jsoup.connect(this.buildURL()).get();
        } catch (IOException e) {
            throw new GCPloenRuntimeException(e, GCPloenRuntimeException.ErrorCode.API_NETWORK,
                    "Could not interface with network.");
        }
        logger.trace("Retrieved website data.");

        Map<GarbageTypes, List<LocalDate>> garbageMap = new HashMap<GarbageTypes, List<LocalDate>>();

        Elements tables = doc.getElementsByTag("table");
        if (tables.toArray().length < 1) {
            logger.warn("Received invalid data from server");
        }
        for (Element table : tables) {
            Element caption = table.getElementsByTag("caption").first();
            String month = caption.text().split(" ")[0];
            int monthAsInt = 0;

            switch (month) {
                case "Januar":
                    monthAsInt = 1;
                    break;
                case "Februar":
                    monthAsInt = 2;
                    break;
                case "März":
                    monthAsInt = 3;
                    break;
                case "April":
                    monthAsInt = 4;
                    break;
                case "Mai":
                    monthAsInt = 5;
                    break;
                case "Juni":
                    monthAsInt = 6;
                    break;
                case "Juli":
                    monthAsInt = 7;
                    break;
                case "August":
                    monthAsInt = 8;
                    break;
                case "September":
                    monthAsInt = 9;
                    break;
                case "Oktober":
                    monthAsInt = 10;
                    break;
                case "November":
                    monthAsInt = 11;
                    break;
                case "Dezember":
                    monthAsInt = 12;
                    break;
                default:
                    logger.warn("Invalid month string in table data from server.");
                    continue;
            }

            int finalMonthAsInt = monthAsInt;
            Arrays.asList(GarbageTypes.values()).forEach(garbageType -> {
                Elements garbageDays = table.getElementsByClass(garbageType.name());

                garbageDays.forEach(garbageDay -> {
                    String dayAsString = garbageDay.parent().ownText();
                    int dayAsInt = Integer.parseInt(dayAsString.substring(0, dayAsString.length() - 1));
                    LocalDate date = LocalDate.of(LocalDate.now().getYear(), finalMonthAsInt, dayAsInt);
                    garbageMap.merge(garbageType, new ArrayList<LocalDate>(List.of(date)), (list1, list2) -> {
                        list1.addAll(list2);
                        return list1;
                    });
                });
            });

        }

        return garbageMap;
    }
}
