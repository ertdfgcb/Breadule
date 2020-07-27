(ns breadule.db)

(def new-stage
  {:editing true
   :name ""
   :waitTime 0
   :workTime 0
   :instructions ""
   :notes ""})

(def testSchedule
  {:name "Test Schedule"
   :stages [{:editing false
             :name "Turn set 1"
             :waitTime 030
             :workTime 005
             :instructions "Stretch n' fold"
             :notes ""}]})

(def default-db
{:name "Breadule"
 :isRunning false
 :schedules {:testId testSchedule}})
