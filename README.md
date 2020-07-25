# MVP

Breadule! An app that handles scheduling and alerting for breadmaking.

## Features

At it's core, this is a fancy alarm clock app. That and notes are the most important parts. The rest is window dressing.
The justification for using this over setting a bunch of alarms/timers:
- You have to calculate in work time, or remeber to set the next timer after you do the work
- Bredule can take notes
- Sometimes the schedule changes based on how the dough is
- There are a lot of timers/alarms over a long period, saving groups of them is nice

### Ideal Feature List
- Alarms for each stage
  - 'Gentle' alarms
    - Alarm goes off, you have 10 minutes (configurable) before next one starts
    - Or you can start manually
- Batched alarms
  - For example "Stretch/fold every 30 minutes x 4"
- Reminders
  - For upcoming alarms
  - Non-alarm reminders (e.g. "Check dough" reminder every 40 minutes during proofing stage)
- Browser notifications
- Email/Text alerts
- Notes before, during, after on each stage
- Andriod/iOS app
- Sync across platforms
- Export
- History

### MVP Feature List
- Stages creation interface
  - Schedules can be reused
  - Alarms for each stage
    - Simple alarms with snooze that pushes all remaining alarms back
  - Instructions for each stage
- Schedule execution interface
  - Shows current period (wait/work) very large
  - Upcoming periods are smaller but also visible
  - Notes can be added on the fly
  - Wait period can be modified on the fly
- Browser notifications
  - Page must be open to alert user
  - But if the page closes, it will pick up where it left off when reopened
- Log Viewing Interface
  - Frozen version of stages viewer
  - Displays notes, alarm end times

## DB

For the DB I'm going to use Mongo. The main reasons are it's simple, I know how to use it, and stores/returns JSON natively. I want to use JSON because it converts to Clojurescript objects very easily. I could use a relational database, but the nature of the project means the schema will change frequently and there are a lot of potential "holes" in the db (e.g. schedules wher eno notes were taken).

The basic object is a "schedule", which consists of a bunch of stages
- Stages represent a period of waiting followed by a period of work
  - For example, waiting 2 hours for autolyse then mixing levain/autolyse
  - The timer duration is the time before work starts
  - The work duration is the time where work is done, before next stage starts
  - Stages also store before notes (aka instructions), and during notes (just notes)

### Schema

`{
  stages: [
    {
      waitTime: Number,
      workTime: Number,
      instructions: String,
      notes: String
    }
  ]
}`
