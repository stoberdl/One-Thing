import { useState } from "react";
import { Check } from "lucide-react";

interface TaskCircleProps {
  isCompleted: boolean;
  onClick: () => void;
  size?: "normal" | "small";
}

const TaskCircle = ({
  isCompleted,
  onClick,
  size = "normal",
}: TaskCircleProps) => {
  const [isAnimating, setIsAnimating] = useState(false);
  const sizeClasses = size === "normal" ? "w-7 h-7" : "w-4 h-4";
  const handleClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    setIsAnimating(true);
    setTimeout(() => setIsAnimating(false), 300);
    onClick();
  };
  return (
    <button
      onClick={handleClick}
      className={`${sizeClasses} rounded-full border-2 flex items-center justify-center transition-all duration-200 ${
        isCompleted
          ? "bg-emerald-500 border-emerald-500"
          : "bg-transparent border-slate-600 hover:border-slate-400"
      } ${isAnimating ? "scale-125" : ""} active:scale-90`}
    >
      {isCompleted && (
        <Check size={size === "normal" ? 16 : 10} className="text-white" />
      )}
    </button>
  );
};
export default TaskCircle;
