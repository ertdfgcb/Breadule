(ns breadule.db)

(def testSchedule
  {:name "Test Schedule"
   :stages [{:name "Turn set 1"
             :waitTime 030
             :workTime 005
             :instructions "Stretch n' fold"
             :notes ""}]})

(def default-db
{:name "Breadule"
 :isRunning false
 :schedules {:testId testSchedule}})
