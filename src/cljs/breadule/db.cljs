(ns breadule.db)

(def new-stage
  {:editing true
   :name ""
   :waitTime 0
   :workTime 0
   :instructions ""
   :notes ""})

(def new-schedule
  {:editing true
   :name ""
   :notes ""
   :stages []})

(def testSchedule
  {:editing false
   :name "Test Schedule"
   :notes "hardcoded test schedule"
   :stages [{:editing false
             :name "Turn set 1"
             :waitTime 1
             :workTime 005
             :instructions "Stretch n' fold"
             :notes ""}]})

(def default-db
{:name "Breadule"
 :running false
 :paused false
 :timer 0
 :currentStage 0
 :phase ""
 :currentSchedule nil
 :schedules {:testId testSchedule}})
