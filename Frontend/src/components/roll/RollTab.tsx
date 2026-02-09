import { useState } from "react";
import { Clock, Sparkles } from "lucide-react";
import type { TimeBracket, Task } from "../../types";
import RollingAnimation from "../common/RollingAnimation";

interface RollTabProps {
  onTaskSelected: (task: any) => void;
}

const RollTab = ({ onTaskSelected }: RollTabProps) => {
  const [selectedTime, setSelectedTime] = useState<TimeBracket | null>(null);
  const [isRolling, setIsRolling] = useState(false);
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);

  const handleTimeSelect = (time: TimeBracket) => {
    setSelectedTime(time);
    setIsRolling(true);
  };

  const handleRollComplete = () => {
    setIsRolling(false);
    setSelectedTask({
      id: 1,
      name: "Clean the kitchen",
      categoryId: 1,
      timeBracket: selectedTime!,
      priority: 3,
      frequency: "WEEKLY",
      createdAt: new Date().toISOString(),
      lastCompleted: null,
      prevCompleted: null,
      parentId: null,
    });
  };

  return (
    <div className="flex-1 flex flex-col items-center justify-center p-4 pb-24">
      {isRolling ? (
        <RollingAnimation
          onComplete={handleRollComplete}
          isRandomMode={false}
        />
      ) : selectedTask ? (
        <div className="w-full max-w-md text-center space-y-6">
          <p className="text-slate-400">Your one thing:</p>
          <h2 className="text-3xl font-bold text-white">{selectedTask.name}</h2>
          <div className="flex gap-3 justify-center">
            <button
              onClick={() => setSelectedTask(null)}
              className="px-6 py-3 rounded-xl bg-slate-700 text-white"
            >
              Pick Again
            </button>
            <button
              onClick={() => {
                onTaskSelected(selectedTask);
                setSelectedTask(null);
              }}
              className="px-6 py-3 rounded-xl bg-indigo-600 text-white"
            >
              Let's Do It
            </button>
          </div>
        </div>
      ) : (
        <div className="w-full max-w-md text-center space-y-8">
          <div className="space-y-3">
            <div className="w-24 h-24 mx-auto rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center shadow-xl shadow-indigo-500/30">
              <Sparkles size={40} className="text-white" />
            </div>
            <h2 className="text-4xl font-bold bg-gradient-to-r from-white to-slate-300 bg-clip-text text-transparent">
              One Thing
            </h2>
            <p className="text-slate-400 text-lg">
              Pick something from your backlog
            </p>
          </div>

          <div className="space-y-4">
            <p className="text-slate-500 font-medium">
              How much time do you have?
            </p>
            <div className="grid grid-cols-3 gap-4">
              {(["<15", "15-30", "30+"] as TimeBracket[]).map((time) => (
                <button
                  key={time}
                  onClick={() => handleTimeSelect(time)}
                  className="group p-6 rounded-3xl transition-all duration-200 hover:scale-105 active:scale-95 border shadow-lg hover:shadow-xl bg-slate-800 border-slate-700"
                >
                  <Clock size={28} className="mx-auto mb-2 text-indigo-400" />
                  <span className="block text-2xl font-bold text-white">
                    {time}
                  </span>
                  <span className="block text-sm text-slate-400">minutes</span>
                </button>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default RollTab;
