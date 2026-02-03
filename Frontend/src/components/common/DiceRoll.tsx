import { useState, useEffect } from "react";

const DiceRoll = () => {
  const [face, setFace] = useState(1);

  useEffect(() => {
    const interval = setInterval(() => {
      setFace(Math.floor(Math.random() * 6) + 1);
    }, 80);
    return () => clearInterval(interval);
  }, []);

  const dots: Record<number, number[][]> = {
    1: [[1, 1]],
    2: [
      [0, 0],
      [2, 2],
    ],
    3: [
      [0, 0],
      [1, 1],
      [2, 2],
    ],
    4: [
      [0, 0],
      [0, 2],
      [2, 0],
      [2, 2],
    ],
    5: [
      [0, 0],
      [0, 2],
      [1, 1],
      [2, 0],
      [2, 2],
    ],
    6: [
      [0, 0],
      [0, 1],
      [0, 2],
      [2, 0],
      [2, 1],
      [2, 2],
    ],
  };
  return (
    <div
      className="w-20 h-20 bg-white rounded-2xl shadow-xl flex items-center justify-center p-3 animate-bounce"
      style={{ animationDuration: "0.3s" }}
    >
      <div className="grid grid-cols-3 gap-1.5 w-full h-full">
        {[0, 1, 2].map((row) =>
          [0, 1, 2].map((col) => {
            const hasDot = dots[face].some(([r, c]) => r === row && c === col);
            return (
              <div
                key={`${row}-${col}`}
                className="flex items-center justify-center"
              >
                {hasDot && (
                  <div className="w-3 h-3 bg-indigo-600 rounded-full" />
                )}
              </div>
            );
          }),
        )}
      </div>
    </div>
  );
};
export default DiceRoll;
