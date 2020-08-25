// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;


public final class FindMeetingQuery {

    /**
    * Returns a Collection of optional TimeRange slots for the requested meeting 
    * If one or more time slots exists so that both mandatory and optional attendees can attend,
    * return those time slots. Otherwise, return the time slots that fit just the mandatory attendees.
    */
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        // Check for optional time slots with optional and mandatory attendees
        Set<String> allAttendees = new HashSet<>(request.getAttendees());
        allAttendees.addAll(request.getOptionalAttendees());
        Collection<TimeRange> allAttendeesOptSlots = 
            getPossibleTimeRangesForAttendees(events, request, allAttendees);
        // No available slots for both mandatory and optional atendees, check only the mandatory
        if (allAttendeesOptSlots.isEmpty()) {
            return getPossibleTimeRangesForAttendees(events, request, request.getAttendees());
        }
        // Available slots for both mandatory and optional attendees
        return allAttendeesOptSlots;
    }

    /**
    * Returns a Collection of optional TimeRange slots for the requested meeting 
    * (when all the meeting attendees are free)
    */
    public Collection<TimeRange> getPossibleTimeRangesForAttendees(Collection<Event> events, 
                                                                   MeetingRequest request,
                                                                   Collection<String> meetingAttendees) {
        // Block slots
        List<TimeRange> badTimeRanges = getBadTimeRanges(events, meetingAttendees);
        badTimeRanges.sort(TimeRange.ORDER_BY_START);
        return getPossibleTimeRanges(badTimeRanges, request.getDuration());
    }

    /**
    * Returns a List of Blocked TimeRange slots for the given meeting attendees
    * (at least one of the meeting attendees go to an event from the given events
    * collection in this time slot) 
    */
    public List<TimeRange> getBadTimeRanges(Collection<Event> events, Collection<String> meetingAttendees) {
        List<TimeRange> badTimeRanges = new ArrayList<>();
        
        for (Event event: events) {
            Set<String> intersection = new HashSet<>(event.getAttendees());
            intersection.retainAll(meetingAttendees);
            // At least one of the meeting attendees go to this event
            if (!intersection.isEmpty()) {
                badTimeRanges.add(event.getWhen());
            }
        }
        return badTimeRanges;
    }

    /**
    * Returns a Collection of optional TimeRange slots for the requested meeting duration
    * between the given bad TimeRange slots
    */
    public Collection<TimeRange> getPossibleTimeRanges(List<TimeRange> badTimeRanges, long duration) {
        Collection<TimeRange> possibleTimeRanges = new ArrayList<>();
        int currStart=TimeRange.START_OF_DAY;
        int currEnd;
        for (TimeRange time: badTimeRanges) {
            currEnd = time.start();
            // Current slot is optional for the given duration
            if (currEnd-currStart >= duration) {
                possibleTimeRanges.add(TimeRange.fromStartEnd(currStart, currEnd, false));
            }
            // Promotes currStart to next free slot beginning or
            // stays put if previous badTimeSlot contains the current
            currStart = Math.max(currStart, time.end());
        }
        // Check slot until end of the day
        if (TimeRange.END_OF_DAY - currStart >= duration) {
            possibleTimeRanges.add(TimeRange.fromStartEnd(currStart, TimeRange.END_OF_DAY, true));
        }
        return possibleTimeRanges;
    }
}
