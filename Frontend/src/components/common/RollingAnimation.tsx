import { useEffect } from 'react';
import SparkleSpinner from './SparkleSpinner';
import DiceRoll from './DiceRoll';

   interface RollingAnimationProps {
    onComplete: () => void;
    isRandomMode: boolean;
  }
 
 const RollingAnimation = ({ onComplete, isRandomMode }: RollingAnimationProps) => {
    useEffect(() => {
      const timer = setTimeout(onComplete, 1000);
      return () => clearTimeout(timer);
    }, [onComplete]);

    return (
      <div className="flex flex-col items-center justify-center space-y-6">
        {isRandomMode ? <DiceRoll /> : <SparkleSpinner />}
        <div className="flex gap-1">
          {[0, 1, 2].map(i => (
            <div
              key={i}
              className="w-2 h-2 bg-indigo-500 rounded-full animate-bounce"
              style={{ animationDelay: `${i * 0.15}s`, animationDuration: '0.5s' }}
            />
          ))}
        </div>
      </div>
    );
  };

  export default RollingAnimation;