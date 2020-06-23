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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/* Given the meeting information, return the times when the meeting could happen that day. */
public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }
    if (request.getAttendees().isEmpty() && request.getOptionalAttendees().isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    if (events.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    ArrayList<TimeRange> allAttendeesEventTimes = new ArrayList<TimeRange>();
    ArrayList<TimeRange> mandatoryAttendeesEventTimes = new ArrayList<TimeRange>();
    long duration = request.getDuration();

    for (Event event : events) {
      // Only take the events into account that are owned by people actually invited to the meeting.
      ArrayList<String> relevantAttendeesForAll = new ArrayList<String>(request.getAttendees());
      relevantAttendeesForAll.retainAll(event.getAttendees());
      if (relevantAttendeesForAll.isEmpty()) {
        ArrayList<String> relevantAttendeesForMandatory = new ArrayList<String>(request.getOptionalAttendees());
        relevantAttendeesForMandatory.retainAll(event.getAttendees());
        if (!relevantAttendeesForMandatory.isEmpty()) {
          allAttendeesEventTimes.add(event.getWhen());
        }
        continue;
      }
      allAttendeesEventTimes.add(event.getWhen());
      mandatoryAttendeesEventTimes.add(event.getWhen());
    }

    Collection<TimeRange> availableTimesForAll = findAvailableTimes(allAttendeesEventTimes, duration);
    if (availableTimesForAll.isEmpty()) {
      // Don't consider the optional attendees if a schedule can't be made including them.
      return findAvailableTimes(mandatoryAttendeesEventTimes, duration);
    }
    return availableTimesForAll;
  }

  /* Find the available times for a meeting */
  private Collection<TimeRange> findAvailableTimes(ArrayList<TimeRange> eventTimes, long duration) {
    eventTimes.add(TimeRange.fromStartDuration(TimeRange.END_OF_DAY + 1, 0));
    Collections.sort(eventTimes, TimeRange.ORDER_BY_START);
    int startTime = TimeRange.START_OF_DAY;
    Collection<TimeRange> availableTimes = new ArrayList<TimeRange>();
    for (TimeRange eventTime : eventTimes) {
      if (eventTime.end() < startTime) {
        continue;
      }
      if (eventTime.start() < startTime) {
        startTime = eventTime.end();
        continue;
      }
      if (eventTime.start() - startTime >= duration) {
        availableTimes.add(TimeRange.fromStartEnd(startTime, eventTime.start(), false));
      }
      startTime = eventTime.end();
    }
    return availableTimes;
  }
}
