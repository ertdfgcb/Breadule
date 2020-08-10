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
   :levain ""
   :dough ""
   :stages []})

(def testSchedule
  {:editing false
   :name "Whole Wheat with Rye"
   :levain "10pm: 200g water, 100g whole wheat, 100g white, 50g starter"
   :dough "6am (pre autolyse): 850g water, 500g whole wheat, 300g rye, 250g white"
   :stages [{:editing false
             :name "Autolyse"
             :waitTime 120
             :workTime 005
             :instructions "Mix levain and 25g salt with dough"
             :notes ""}
            {:editing false
             :name "Turn set 1"
             :waitTime 30
             :workTime 003
             :instructions "Stretch and fold 4 times"
             :notes ""}
            {:editing false
             :name "Turn set 2"
             :waitTime 30
             :workTime 003
             :instructions "Stretch and fold 4 times"
             :notes ""}
            {:editing false
             :name "Turn set 3"
             :waitTime 30
             :workTime 003
             :instructions "Stretch and fold 4 times"
             :notes ""}
            {:editing false
             :name "Turn set 4"
             :waitTime 30
             :workTime 003
             :instructions "Stretch and fold 4 times"
             :notes ""}
            {:editing false
             :name "Turn set 5"
             :waitTime 30
             :workTime 003
             :instructions "Stretch and fold 4 times"
             :notes ""}
            {:editing false
             :name "Bulk ferment"
             :waitTime 120
             :workTime 5
             :instructions "Finish rise, then cut and pre-shape dough"
             :notes ""}
            {:editing false
             :name "Shape"
             :waitTime 20
             :workTime 003
             :instructions "Shape into loaves for proofing"
             :notes ""}
            {:editing false
             :name "Proof"
             :waitTime 120
             :workTime 003
             :instructions "Check and add more time as needed"
             :notes ""}]})

(def default-db
{:name "Breadule"
 :running false
 :paused false
 :timer 0
 :startTimestamp nil
 :currentStage 0
 :phase ""
 :currentSchedule nil
 :schedules {:testId testSchedule}})
