/*
 * This file is part of bisq.
 *
 * bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bisq.protocol.availability.tasks;

import io.bisq.common.taskrunner.Task;
import io.bisq.common.taskrunner.TaskRunner;
import io.bisq.network_messages.availability.AvailabilityResult;
import io.bisq.network_messages.availability.OfferAvailabilityResponse;
import io.bisq.network_messages.trade.offer.payload.OfferPayload;
import io.bisq.p2p.protocol.availability.OfferAvailabilityModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessOfferAvailabilityResponse extends Task<OfferAvailabilityModel> {
    private static final Logger log = LoggerFactory.getLogger(ProcessOfferAvailabilityResponse.class);

    public ProcessOfferAvailabilityResponse(TaskRunner taskHandler, OfferAvailabilityModel model) {
        super(taskHandler, model);
    }

    @Override
    protected void run() {
        try {
            runInterceptHook();
            OfferAvailabilityResponse offerAvailabilityResponse = model.getMessage();

            if (model.offer.getState() != OfferPayload.State.REMOVED) {
                // TODO: isAvailable is kept for backward compatibility. Can be removed once everyone is on v0.4.9
                if (offerAvailabilityResponse.isAvailable || offerAvailabilityResponse.availabilityResult == AvailabilityResult.AVAILABLE) {
                    model.offer.setState(OfferPayload.State.AVAILABLE);
                } else {
                    model.offer.setState(OfferPayload.State.NOT_AVAILABLE);
                    failed("Take offer attempt rejected because of: " + offerAvailabilityResponse.availabilityResult);
                }
            }

            complete();
        } catch (Throwable t) {
            model.offer.setErrorMessage("An error occurred.\n" +
                    "Error message:\n"
                    + t.getMessage());

            failed(t);
        }
    }
}