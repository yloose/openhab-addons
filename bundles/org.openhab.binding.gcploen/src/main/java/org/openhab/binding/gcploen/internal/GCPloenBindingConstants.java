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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link GCPloenBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Yan Loose - Initial contribution
 */
@NonNullByDefault
public class GCPloenBindingConstants {

    private static final String BINDING_ID = "gcploen";

    // List of all Thing Type UIDs
    public static final ThingTypeUID GCPLOEN_THING_TYPE_UID = new ThingTypeUID(BINDING_ID, "gcploenThing");

    // List of all Channel ids
    public static final String CHANNEL_ABFALLART_1_1 = "abfallart_1_1";
    public static final String CHANNEL_ABFALLART_1_2 = "abfallart_1_2";
    public static final String CHANNEL_ABFALLART_1_3 = "abfallart_1_3";
    public static final String CHANNEL_ABFALLART_1_4 = "abfallart_1_4";
    public static final String CHANNEL_ABFALLART_1_5 = "abfallart_1_5";
    public static final String CHANNEL_ABFALLART_1_6 = "abfallart_1_6";
    public static final String CHANNEL_ABFALLART_1_7 = "abfallart_1_7";
}
