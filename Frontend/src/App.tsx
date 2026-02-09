import { useState } from "react";
import { List, Dices } from "lucide-react";
import RollTab from "./components/roll/RollTab";

function App() {
  const [activeTab, setActiveTab] = useState<"roll" | "tasks">("roll");

  const handleTaskSelected = (task: any) => {
    console.log("Task selected:", task);
  };

  return (
    <div className="min-h-screen bg-slate-900 text-white flex flex-col">
      <main className="flex-1 flex">
        {activeTab === "roll" ? (
          <RollTab onTaskSelected={handleTaskSelected} />
        ) : (
          <div className="p-4 text-center">Tasks Tab (coming soon)</div>
        )}
      </main>

      <nav className="fixed bottom-0 left-0 right-0 bg-slate-800 border-t border-slate-700">
        <div className="flex">
          <button
            onClick={() => setActiveTab("roll")}
            className={`flex-1 py-4 flex flex-col items-center gap-1 ${
              activeTab === "roll" ? "text-indigo-400" : "text-slate-500"
            }`}
          >
            <Dices size={24} />
            <span className="text-xs">Roll</span>
          </button>
          <button
            onClick={() => setActiveTab("tasks")}
            className={`flex-1 py-4 flex flex-col items-center gap-1 ${
              activeTab === "tasks" ? "text-indigo-400" : "text-slate-500"
            }`}
          >
            <List size={24} />
            <span className="text-xs">Tasks</span>
          </button>
        </div>
      </nav>
    </div>
  );
}

export default App;
