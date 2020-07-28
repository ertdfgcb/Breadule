(ns breadule.views.stage
  (:require
   [re-frame.core :as re-frame]
   [re-com.core   :refer [title h-box v-box md-icon-button input-text input-textarea label input-time button]]
   [breadule.subs :as subs]
   [breadule.events :as events]))

(defn stage-field-sub [id num name]
  (re-frame/subscribe [::subs/stage-field id num name]))

(defn labeled [l t]
  [:div [title :level :level3 :label l] t])

(defn stage-edit-buttons [scheduleId num editing]
  (let [edit-start #(re-frame/dispatch
                     [::events/update-stage scheduleId num :editing (not editing)])]
    [[md-icon-button
      :class "stage-edit-button"
      :size :smaller
      :md-icon-name (if editing "zmdi-check" "zmdi-edit")
      :on-click edit-start]
     [md-icon-button
      :class "stage-edit-button"
      :size :smaller
      :md-icon-name "zmdi-delete"
      :on-click #(re-frame/dispatch [::events/remove-stage scheduleId num])]]))

(defn stage-form [scheduleId num]
  (let [fieldSub #(stage-field-sub scheduleId num %)
        fieldUpdate #(re-frame/dispatch [::events/update-stage scheduleId num %1 %2])
        name (fieldSub :name)
        waitTime (fieldSub :waitTime)
        workTime (fieldSub :workTime)
        instructions (fieldSub :instructions)]
    [v-box
     :children [(labeled "Name"
                         [h-box
                          :children
                          (concat [[input-text
                                    :width "320px"
                                    :change-on-blur? false
                                    :model name
                                    :on-change #(fieldUpdate :name %)]]
                                  (stage-edit-buttons scheduleId num true))])
                (labeled "Wait Time"
                         [input-time
                          :width "320px"
                          :model waitTime
                          :on-change #(fieldUpdate :waitTime %)])
                (labeled "Work Time"
                         [input-time
                          :width "320px"
                          :model workTime
                          :on-change #(fieldUpdate :workTime %)])
                (labeled "Instructions"
                         [input-textarea
                          :width "320px"
                          :model instructions
                          :change-on-blur? false
                          :on-change #(fieldUpdate :instructions %)])]]))

(defn stage-title [scheduleId stage num]
  (let [currentStage (re-frame/subscribe [::subs/db-field :currentStage])
        running (re-frame/subscribe [::subs/db-field :running])
        class (if (and @running (= @currentStage num)) "highlighted-title" "")]
  [h-box
   :children (concat [[title :class class :level :level3 :label (:name stage)]]
                     (stage-edit-buttons scheduleId num false))]))

(defn stage-view [scheduleId num stage]
  (let [editing (stage-field-sub scheduleId num :editing)
        ]
    ^{:key num}
    [:div
     (if @editing
       (stage-form scheduleId num)
       [v-box
        :children [(stage-title scheduleId stage num)
                   [:div "Wait " (:waitTime stage) " minutes, then work " (:workTime stage) " minutes."]
                   [:div (:instructions stage)]]])
     [:hr]]))

(defn stages-edit-view [scheduleId schedule]
  [:div
   {:class "stages-edit-view"}
   [h-box
    :children [[title :level :level2 :label "Stages"]
               [md-icon-button
                :class "stage-add-button"
                :md-icon-name "zmdi-plus"
                :on-click #(re-frame/dispatch
                            [::events/add-stage scheduleId])]]]
   (doall (map-indexed #(stage-view scheduleId %1 %2) (:stages schedule)))])
