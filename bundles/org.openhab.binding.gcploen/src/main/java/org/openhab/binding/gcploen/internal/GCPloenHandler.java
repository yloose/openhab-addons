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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;

/**
 * The {@link GCPloenHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Yan Loose - Initial contribution
 */
@NonNullByDefault
public class GCPloenHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(GCPloenHandler.class);

    private @Nullable GCPloenConfiguration config;

    private @Nullable GCPloenWebsiteAPI webAPI;
    private @Nullable ScheduledFuture<?> updateJob;

    public GCPloenHandler(Thing thing) {
        super(thing);
    }

    @SuppressWarnings("null")
    private void updateChannels() {
        logger.debug("Updating channels.");
        assert this.webAPI != null;
        Map<GCPloenWebsiteAPI.GarbageTypes, List<LocalDate>> garbageMap = webAPI.getData();

        logger.trace("Parsing garbage dates to JSON.");
        for (Map.Entry<GCPloenWebsiteAPI.GarbageTypes, List<LocalDate>> garbageType : garbageMap.entrySet()) {
            JsonArray jsonArray = new JsonArray();
            garbageType.getValue().forEach(date -> {
                jsonArray.add(date.toString());
            });
            updateState(garbageType.getKey().name(), new StringType(jsonArray.toString()));
        }
    }

    @Override
    public void channelLinked(ChannelUID channelUID) {
        this.updateChannels();
    }

    @Override
    @SuppressWarnings("null")
    public void initialize() {
        config = getConfigAs(GCPloenConfiguration.class);

        updateStatus(ThingStatus.UNKNOWN);

        scheduler.execute(() -> {

            this.webAPI = new GCPloenWebsiteAPI(config.district, config.street);

            logger.trace("Checking server connectivity.");
            try {
                if (webAPI.checkAvailability()) {
                    updateStatus(ThingStatus.ONLINE);
                }
            } catch (GCPloenRuntimeException e) {
                updateStatus(ThingStatus.OFFLINE, e.getErrorCode().thingStatusDetail, e.getMessage());
            }

        });

        logger.trace("Starting update job.");
        this.updateJob = scheduler.scheduleWithFixedDelay(this::updateChannels, 0, config.refreshInterval,
                TimeUnit.DAYS);
    }

    @Override
    @SuppressWarnings("null")
    public void dispose() {
        if (this.updateJob != null) {
            this.updateJob.cancel(true);
            this.updateJob = null;
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }
}
