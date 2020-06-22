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

    if (events.isEmpty() || request.getAttendees().isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    Collection<String> requestAttendees = request.getAttendees();
    ArrayList<TimeRange> eventTimes = new ArrayList<TimeRange>();
    for (Event event : events) {
      ArrayList<String> relevantAttendees = new ArrayList<String>(request.getAttendees());
      relevantAttendees.retainAll(event.getAttendees());
      if (relevantAttendees.isEmpty()) {
        continue;
      }
      eventTimes.add(event.getWhen());
    }

    eventTimes.add(TimeRange.fromStartDuration(TimeRange.END_OF_DAY + 1, 0));
    Collections.sort(eventTimes, TimeRange.ORDER_BY_START);
    int startTime = TimeRange.START_OF_DAY;
    long duration = request.getDuration();
    Collection<TimeRange> availableTimes = new ArrayList<TimeRange>();
    for (TimeRange eventTime : eventTimes) {
      if (eventTime.end() < startTime) {
        continue;
      }
      if (eventTime.start() < startTime) {
        startTime = eventTime.end();
        continue;
      }
      if (eventTime.start() - startTime >= request.getDuration()) {
        availableTimes.add(TimeRange.fromStartEnd(startTime, eventTime.start(), false));
      }
      startTime = eventTime.end();
    }
    return availableTimes;
  }
}
