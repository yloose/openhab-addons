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
import org.openhab.core.thing.ThingStatusDetail;

/**
 * The {@link GCPloenRuntimeException} is a general runtime exception for the GCPloen binding.
 *
 * @author Yan Loose - Initial contribution
 */
@NonNullByDefault
public class GCPloenRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -1972735595402986139L;

    public enum ErrorCode {
        API_CONFIG(ThingStatusDetail.CONFIGURATION_ERROR),
        API_NETWORK(ThingStatusDetail.COMMUNICATION_ERROR);

        public final ThingStatusDetail thingStatusDetail;

        private ErrorCode(ThingStatusDetail thingStatusDetail) {
            this.thingStatusDetail = thingStatusDetail;
        }
    }

    private final ErrorCode errorCode;

    public GCPloenRuntimeException(Throwable err, ErrorCode errorCode, String errorMessage) {
        super(errorMessage, err);
        this.errorCode = errorCode;
    }

    public GCPloenRuntimeException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
