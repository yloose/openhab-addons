# Garbage Collection Ploen Binding

The GCPloen binding provides information about garbage collection dates for specified streets and communes/district in the area of Ploen. The data is retrieved from the online service provided here: [Kreis Plön Abfallwirtschaft](https://www.kreis-ploen.de/B%C3%BCrgerservice/Onlinedienste/Abfuhrtermine-Abfallwirtschaft/index.php)

> :warning: As such the binding may **break** if the website changes.

## Supported Things

-   **gcploenThing:** Represents the connection to the **Abfallwirtschaft Kreis Plön garbage collection calendar** with channels for each different type of waste totaling at seven.

## Discovery

Discovery is not possible, due to some form input values from the website above being required.

## Thing Configuration

For configuration of the **gcploenThing** you need to specify your district (Ort) and your street (Strasse) as encoded as they are on the web service. To get these configuration parameters follow the steps below.

1. Open [Kreis Plön Abfallwirtschaft](https://www.kreis-ploen.de/B%C3%BCrgerservice/Onlinedienste/Abfuhrtermine-Abfallwirtschaft/index.php) in yoour browser.
2. Select your desired district via the "Ort" selector.
3. Select your desired street via the "Strasse" selector.
4. Click on "Anzeigen".
5. The URL should have now changed and you can retrieve the `[ORT_VALUE]` and `[STRASSE_VALUE]` from it.
6. Lastly enter the values into the corresponding field in the thing configuration.

**Example URL:**
> https://www.kreis-ploen.de/B%C3%BCrgerservice/Onlinedienste/Abfuhrtermine-Abfallwirtschaft/index.php?ort=`[ORT_VALUE]`&strasse=`[STRASSE_VALUE]`

**gcploenThing** parameters:

| Property | Default | Required | Description |
|-|-|-|-|
| `district` | | Yes | The desired district, taken from the field `[ORT_VALUE]`. Looks like `2156.55` |
| `street` | | Yes | The selected street, taken from the field `[STRASSE_VALUE]`. Looks like `2156.642.1` |
| `refreshInterval` | 30 | No | The interval at which the new data is queried in **days**. As each data retrieval provides dates for the rest of the year. |

## Channels

The thing **gcploenThong** provides in total 7 channels, which each represents one garbage type and provides the collection days for the rest of the year encoded in a JSON array in ISO 8601 format.

Example: `[..., 2022-06-04, 2022-06-18, ...]`

| channel  | type   | description                  |
|----------|--------|------------------------------|
| abfallart_1_1  | String | Restabfall 14-täglich collection dates  |
| abfallart_1_2  | String | Restabfall 4-wöchentlich collection dates  |
| abfallart_1_3  | String | Gelber Sack collection dates  |
| abfallart_1_4  | String | Bioabfall 2-wöchentlich collection dates  |
| abfallart_1_5  | String | Papier 4-wöchentlich collection dates  |
| abfallart_1_6  | String | Papiercontainer 4-wöchentlich collection dates  |
| abfallart_1_7  | String | Restabfallcontainer 14-täglich collection dates  |
