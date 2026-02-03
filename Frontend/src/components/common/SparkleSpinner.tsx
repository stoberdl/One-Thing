import { Sparkles } from "lucide-react";

const SparkleSpinner = () => (
  <div className="relative w-20 h-20">
    <div
      className="absolute inset-0 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 animate-spin"
      style={{ animationDuration: "0.6s" }}
    >
      <div className="absolute inset-2 rounded-full bg-slate-900" />
    </div>
    <div className="absolute inset-0 flex items-center justify-center">
      <Sparkles size={32} className="text-indigo-400" />
    </div>
  </div>
);

export default SparkleSpinner;
