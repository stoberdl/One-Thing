import React, { useState, useEffect, useRef } from 'react';
import { RotateCcw, Check, Clock, Sparkles, Home, Dog, Wrench, Leaf, X, Plus, ChevronDown, ChevronRight, List, Sliders, Dices } from 'lucide-react';

const initialTasks = [
  // Living Space / Kitchen
  { id: 1, name: 'Clean floors', category: 'Living Space', time: '15-30', priority: 2, lastCompleted: null, previousCompleted: null, createdAt: Date.now() - 86400000 * 14 },
  { id: 2, name: 'Wipe door handles', category: 'Living Space', time: '<15', priority: 1, lastCompleted: null, previousCompleted: null, createdAt: Date.now() - 86400000 * 7 },
  { id: 3, name: 'Wipe cabinet fronts', category: 'Living Space', time: '<15', priority: 1, lastCompleted: Date.now() - 86400000 * 10, previousCompleted: null, createdAt: Date.now() - 86400000 * 30 },
  { id: 4, name: 'Wash dog bed cover', category: 'Living Space', time: '<15', priority: 2, lastCompleted: null, previousCompleted: null, createdAt: Date.now() - 86400000 * 5 },
  { id: 5, name: 'Clean couch fabric', category: 'Living Space', time: '15-30', priority: 2, lastCompleted: Date.now() - 86400000 * 21, previousCompleted: null, createdAt: Date.now() - 86400000 * 60 },
  { id: 6, name: 'Vacuum everywhere', category: 'Living Space', time: '30+', priority: 3, lastCompleted: Date.now() - 86400000 * 4, previousCompleted: null, createdAt: Date.now() - 86400000 * 30 },
  { id: 7, name: 'Dust ceiling fans', category: 'Living Space', time: '15-30', priority: 1, lastCompleted: null, previousCompleted: null, createdAt: Date.now() - 86400000 * 20 },
  { id: 8, name: 'Clean microwave inside', category: 'Living Space', time: '<15', priority: 1, lastCompleted: Date.now() - 86400000 * 8, previousCompleted: null, createdAt: Date.now() - 86400000 * 15 },
  
  // Dog
  { id: 9, name: 'Line comb mane', category: 'Dog', time: '15-30', priority: 2, lastCompleted: Date.now() - 86400000 * 3, previousCompleted: null, createdAt: Date.now() - 86400000 * 30, parentId: 12 },
  { id: 10, name: 'Line comb legs', category: 'Dog', time: '15-30', priority: 2, lastCompleted: null, previousCompleted: null, createdAt: Date.now() - 86400000 * 30, parentId: 12 },
  { id: 11, name: 'Trim nails', category: 'Dog', time: '<15', priority: 3, lastCompleted: Date.now() - 86400000 * 14, previousCompleted: null, createdAt: Date.now() - 86400000 * 45 },
  { id: 12, name: 'Full line comb session', category: 'Dog', time: '30+', priority: 2, lastCompleted: null, previousCompleted: null, createdAt: Date.now() - 86400000 * 30, childIds: [9, 10] },
  { id: 13, name: 'Clean ears', category: 'Dog', time: '<15', priority: 2, lastCompleted: Date.now() - 86400000 * 7, previousCompleted: null, createdAt: Date.now() - 86400000 * 20 },
  { id: 14, name: 'Brush teeth', category: 'Dog', time: '<15', priority: 2, lastCompleted: null, previousCompleted: null, createdAt: Date.now() - 86400000 * 10 },
  
  // Maintenance
  { id: 15, name: 'Check smoke detectors', category: 'Maintenance', time: '<15', priority: 3, lastCompleted: null, previousCompleted: null, createdAt: Date.now() - 86400000 * 60 },
  { id: 16, name: 'Clean dryer vent', category: 'Maintenance', time: '15-30', priority: 3, lastCompleted: Date.now() - 86400000 * 45, previousCompleted: null, createdAt: Date.now() - 86400000 * 90 },
  { id: 17, name: 'Replace AC filter', category: 'Maintenance', time: '<15', priority: 2, lastCompleted: Date.now() - 86400000 * 30, previousCompleted: null, createdAt: Date.now() - 86400000 * 60 },
  { id: 18, name: 'Flush water heater', category: 'Maintenance', time: '30+', priority: 1, lastCompleted: null, previousCompleted: null, createdAt: Date.now() - 86400000 * 120 },
  
  // Outdoor
  { id: 19, name: 'Pull weeds in front', category: 'Outdoor', time: '15-30', priority: 1, lastCompleted: Date.now() - 86400000 * 12, previousCompleted: null, createdAt: Date.now() - 86400000 * 30 },
  { id: 20, name: 'Clean porch furniture', category: 'Outdoor', time: '15-30', priority: 1, lastCompleted: null, previousCompleted: null, createdAt: Date.now() - 86400000 * 25 },
  { id: 21, name: 'Check gutters', category: 'Outdoor', time: '30+', priority: 2, lastCompleted: null, previousCompleted: null, createdAt: Date.now() - 86400000 * 90 },
];

const categoryIcons = {
  'Living Space': Home,
  'Dog': Dog,
  'Maintenance': Wrench,
  'Outdoor': Leaf,
};

const categoryColors = {
  'Living Space': { bg: 'bg-blue-500', light: 'bg-blue-500/20', text: 'text-blue-400', border: 'border-blue-500/30' },
  'Dog': { bg: 'bg-amber-500', light: 'bg-amber-500/20', text: 'text-amber-400', border: 'border-amber-500/30' },
  'Maintenance': { bg: 'bg-slate-400', light: 'bg-slate-400/20', text: 'text-slate-300', border: 'border-slate-400/30' },
  'Outdoor': { bg: 'bg-emerald-500', light: 'bg-emerald-500/20', text: 'text-emerald-400', border: 'border-emerald-500/30' },
};

const timeColors = {
  '<15': { bg: 'bg-teal-500/20', text: 'text-teal-400', border: 'border-teal-500/40' },
  '15-30': { bg: 'bg-violet-500/20', text: 'text-violet-400', border: 'border-violet-500/40' },
  '30+': { bg: 'bg-rose-500/20', text: 'text-rose-400', border: 'border-rose-500/40' },
};

const commonTaskSuggestions = {
  'Living Space': ['Mop kitchen', 'Organize closet', 'Clean windows', 'Declutter drawer', 'Wash curtains', 'Clean oven', 'Organize pantry', 'Dust shelves', 'Clean baseboards', 'Wipe light switches'],
  'Dog': ['Bath time', 'Clean water bowl', 'Wash toys', 'Check flea treatment', 'Paw balm application', 'Deshed brushing', 'Clean food bowl', 'Wash collar', 'Dental chew'],
  'Maintenance': ['Test CO detector', 'Clean garbage disposal', 'Check fire extinguisher', 'Lubricate hinges', 'Clean range hood filter', 'Check weather stripping', 'Test GFCI outlets'],
  'Outdoor': ['Sweep driveway', 'Clean outdoor lights', 'Trim hedges', 'Power wash walkway', 'Clean bird feeder', 'Check sprinklers', 'Clean grill', 'Tidy garage'],
};

const ProgressCircle = ({ percentage, size = 32 }) => {
  const radius = (size - 4) / 2;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (percentage / 100) * circumference;
  
  return (
    <svg width={size} height={size} className="transform -rotate-90">
      <circle cx={size / 2} cy={size / 2} r={radius} fill="none" stroke="currentColor" strokeWidth="3" className="text-slate-700" />
      <circle cx={size / 2} cy={size / 2} r={radius} fill="none" stroke="currentColor" strokeWidth="3" strokeDasharray={circumference} strokeDashoffset={offset} strokeLinecap="round" className="text-indigo-500 transition-all duration-500" />
    </svg>
  );
};

const TaskCircle = ({ isCompleted, onClick, size = 'normal' }) => {
  const [isAnimating, setIsAnimating] = useState(false);
  
  const handleClick = (e) => {
    e.stopPropagation();
    setIsAnimating(true);
    setTimeout(() => setIsAnimating(false), 300);
    onClick();
  };
  
  const sizeClasses = size === 'normal' ? 'w-7 h-7' : 'w-4 h-4';
  
  return (
    <button
      onClick={handleClick}
      className={`${sizeClasses} rounded-full border-2 flex items-center justify-center transition-all duration-200 ${
        isCompleted ? 'bg-emerald-500 border-emerald-500' : 'bg-transparent border-slate-600 hover:border-slate-400'
      } ${isAnimating ? 'scale-125' : ''} active:scale-90`}
    >
      {isCompleted && <Check size={size === 'normal' ? 16 : 10} className="text-white" />}
    </button>
  );
};

const DiceRoll = () => {
  const [face, setFace] = useState(1);
  
  useEffect(() => {
    const interval = setInterval(() => {
      setFace(Math.floor(Math.random() * 6) + 1);
    }, 80);
    return () => clearInterval(interval);
  }, []);

  const dots = {
    1: [[1, 1]],
    2: [[0, 0], [2, 2]],
    3: [[0, 0], [1, 1], [2, 2]],
    4: [[0, 0], [0, 2], [2, 0], [2, 2]],
    5: [[0, 0], [0, 2], [1, 1], [2, 0], [2, 2]],
    6: [[0, 0], [0, 1], [0, 2], [2, 0], [2, 1], [2, 2]],
  };

  return (
    <div className="w-20 h-20 bg-white rounded-2xl shadow-xl flex items-center justify-center p-3 animate-bounce" style={{ animationDuration: '0.3s' }}>
      <div className="grid grid-cols-3 gap-1.5 w-full h-full">
        {[0, 1, 2].map(row => (
          [0, 1, 2].map(col => {
            const hasDot = dots[face].some(([r, c]) => r === row && c === col);
            return (
              <div key={`${row}-${col}`} className="flex items-center justify-center">
                {hasDot && <div className="w-3 h-3 bg-indigo-600 rounded-full" />}
              </div>
            );
          })
        ))}
      </div>
    </div>
  );
};

const SparkleSpinner = () => (
  <div className="relative w-20 h-20">
    <div className="absolute inset-0 rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 animate-spin" style={{ animationDuration: '0.6s' }}>
      <div className="absolute inset-2 rounded-full bg-slate-900" />
    </div>
    <div className="absolute inset-0 flex items-center justify-center">
      <Sparkles size={32} className="text-indigo-400" />
    </div>
  </div>
);

const RollingAnimation = ({ onComplete, isRandomMode }) => {
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

// Autocomplete Input Component
const AutocompleteInput = ({ value, onChange, suggestions, onSubmit, placeholder }) => {
  const [selectedIndex, setSelectedIndex] = useState(0);
  const inputRef = useRef(null);

  // Reset selected index when suggestions change
  useEffect(() => {
    setSelectedIndex(0);
  }, [suggestions.length, value]);

  const currentSuggestion = suggestions[selectedIndex] || '';
  
  // Get the ghost text (the part of suggestion that extends beyond what user typed)
  const getGhostText = () => {
    if (!currentSuggestion) return '';
    if (value.length === 0) return currentSuggestion;
    
    const lowerValue = value.toLowerCase();
    const lowerSuggestion = currentSuggestion.toLowerCase();
    
    if (lowerSuggestion.startsWith(lowerValue)) {
      return currentSuggestion.slice(value.length);
    }
    return '';
  };

  const ghostText = getGhostText();
  const showGhost = suggestions.length > 0 && (value.length === 0 || ghostText.length > 0);

  const handleKeyDown = (e) => {
    if (e.key === 'Tab' && showGhost && currentSuggestion) {
      e.preventDefault();
      onChange(currentSuggestion);
    } else if (e.key === 'ArrowDown') {
      e.preventDefault();
      setSelectedIndex(prev => (prev + 1) % Math.max(suggestions.length, 1));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setSelectedIndex(prev => (prev - 1 + suggestions.length) % Math.max(suggestions.length, 1));
    } else if (e.key === 'Enter' && value.trim()) {
      e.preventDefault();
      onSubmit();
    }
  };

  return (
    <div className="relative">
      {/* Ghost text layer */}
      <div className="absolute inset-0 px-4 py-3 pointer-events-none flex items-center">
        <span className="text-transparent">{value}</span>
        {showGhost && (
          <span className="text-slate-600">{value.length === 0 ? currentSuggestion : ghostText}</span>
        )}
      </div>
      
      {/* Actual input */}
      <input
        ref={inputRef}
        type="text"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        onKeyDown={handleKeyDown}
        placeholder={!showGhost ? placeholder : ''}
        className="w-full px-4 py-3 bg-transparent border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500 relative z-10"
        autoFocus
      />
      
      {/* Hint text */}
      {showGhost && suggestions.length > 0 && (
        <div className="absolute right-3 top-1/2 -translate-y-1/2 flex items-center gap-2 text-xs text-slate-500 pointer-events-none z-20">
          <span className="bg-slate-800 px-1.5 py-0.5 rounded text-[10px]">Tab</span>
          {suggestions.length > 1 && (
            <>
              <span className="bg-slate-800 px-1.5 py-0.5 rounded text-[10px]">↑↓</span>
              <span className="text-slate-600">{selectedIndex + 1}/{suggestions.length}</span>
            </>
          )}
        </div>
      )}
    </div>
  );
};

export default function OneThingApp() {
  const [tasks, setTasks] = useState(initialTasks);
  const [selectedTime, setSelectedTime] = useState(null);
  const [currentTask, setCurrentTask] = useState(null);
  const [justCompleted, setJustCompleted] = useState(false);
  const [showSettings, setShowSettings] = useState(false);
  const [randomness, setRandomness] = useState(50);
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [activeTab, setActiveTab] = useState('roll');
  const [expandedCategories, setExpandedCategories] = useState(['Living Space', 'Dog', 'Maintenance', 'Outdoor']);
  const [addingToCategory, setAddingToCategory] = useState(null);
  const [newTaskName, setNewTaskName] = useState('');
  const [newTaskTime, setNewTaskTime] = useState('<15');
  const [isRolling, setIsRolling] = useState(false);

  const categories = [...new Set(tasks.map(t => t.category))];

  const getDaysSince = (timestamp) => {
    if (!timestamp) return null;
    return Math.floor((Date.now() - timestamp) / 86400000);
  };

  const isRecentlyCompleted = (task) => task.lastCompleted && getDaysSince(task.lastCompleted) < 7;

  const calculateEffectivePriority = (task) => {
    const baseTime = task.lastCompleted || task.createdAt;
    const daysSince = getDaysSince(baseTime);
    const ageFactor = Math.min(daysSince / 7, 5);
    return task.priority + ageFactor;
  };

  const getSubtaskProgress = (parentTask) => {
    if (!parentTask.childIds) return null;
    const children = tasks.filter(t => parentTask.childIds.includes(t.id));
    const completedRecently = children.filter(t => isRecentlyCompleted(t)).length;
    return Math.round((completedRecently / children.length) * 100);
  };

  const getFilteredSuggestions = (category) => {
    if (!commonTaskSuggestions[category]) return [];
    const existingNames = tasks.filter(t => t.category === category).map(t => t.name.toLowerCase());
    return commonTaskSuggestions[category].filter(s => {
      const matchesSearch = newTaskName.length === 0 || s.toLowerCase().startsWith(newTaskName.toLowerCase());
      const notExists = !existingNames.includes(s.toLowerCase());
      return matchesSearch && notExists;
    });
  };

  const pickTaskForTime = (time) => {
    let eligible = tasks.filter(t => {
      const timeMatch = t.time === time;
      const categoryMatch = selectedCategory === 'All' || t.category === selectedCategory;
      return timeMatch && categoryMatch;
    });
    if (eligible.length === 0) return null;
    const priorityWeight = (100 - randomness) / 100;
    const weighted = eligible.map(t => ({ ...t, weight: 1 + (calculateEffectivePriority(t) * priorityWeight * 2) }));
    const totalWeight = weighted.reduce((sum, t) => sum + t.weight, 0);
    let random = Math.random() * totalWeight;
    for (const task of weighted) {
      random -= task.weight;
      if (random <= 0) return task;
    }
    return weighted[0];
  };

  const handleTimeSelect = (time) => {
    setSelectedTime(time);
    setJustCompleted(false);
    setIsRolling(true);
  };

  const handleRollComplete = () => {
    setIsRolling(false);
    const task = pickTaskForTime(selectedTime);
    setCurrentTask(task);
  };

  const handleReroll = () => {
    setJustCompleted(false);
    setIsRolling(true);
  };

  const handleComplete = () => {
    if (!currentTask) return;
    const now = Date.now();
    setTasks(prev => prev.map(t => {
      if (t.id === currentTask.id) {
        return { ...t, previousCompleted: t.lastCompleted, lastCompleted: now };
      }
      if (currentTask.childIds?.includes(t.id)) {
        return { ...t, previousCompleted: t.lastCompleted, lastCompleted: now };
      }
      return t;
    }));
    setJustCompleted(true);
  };

  const toggleTaskCompletion = (taskId) => {
    setTasks(prev => prev.map(t => {
      if (t.id === taskId) {
        if (isRecentlyCompleted(t)) {
          return { ...t, lastCompleted: t.previousCompleted, previousCompleted: null };
        } else {
          return { ...t, previousCompleted: t.lastCompleted, lastCompleted: Date.now() };
        }
      }
      return t;
    }));
  };

  const handleAddTask = (category) => {
    if (!newTaskName.trim()) return;
    const newTask = {
      id: Date.now(),
      name: newTaskName.trim(),
      category,
      time: newTaskTime,
      priority: 2,
      lastCompleted: null,
      previousCompleted: null,
      createdAt: Date.now(),
    };
    setTasks(prev => [...prev, newTask]);
    setNewTaskName('');
    setAddingToCategory(null);
  };

  const formatDate = (timestamp) => {
    if (!timestamp) return 'Never';
    const days = getDaysSince(timestamp);
    if (days === 0) return 'Today';
    if (days === 1) return 'Yesterday';
    if (days < 7) return `${days}d ago`;
    if (days < 30) return `${Math.floor(days / 7)}w ago`;
    return `${Math.floor(days / 30)}mo ago`;
  };

  const getTimeLabel = (time) => {
    if (time === '<15') return '<15m';
    if (time === '15-30') return '15-30m';
    return '30+m';
  };

  const toggleCategory = (cat) => {
    setExpandedCategories(prev => prev.includes(cat) ? prev.filter(c => c !== cat) : [...prev, cat]);
  };

  const CategoryIcon = currentTask ? categoryIcons[currentTask.category] : Home;
  const isRandomMode = randomness >= 66;

  const RollTab = () => (
    <div className="flex-1 flex flex-col items-center justify-center p-4 pb-24">
      {isRolling ? (
        <RollingAnimation onComplete={handleRollComplete} isRandomMode={isRandomMode} />
      ) : !currentTask && !selectedTime ? (
        <div className="w-full max-w-md text-center space-y-8">
          <div className="space-y-3">
            <div className="w-24 h-24 mx-auto rounded-full bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center shadow-xl shadow-indigo-500/30">
              <Sparkles size={40} className="text-white" />
            </div>
            <h2 className="text-4xl font-bold bg-gradient-to-r from-white to-slate-300 bg-clip-text text-transparent">One Thing</h2>
            <p className="text-slate-400 text-lg">Pick something from your backlog</p>
            {selectedCategory !== 'All' && (
              <span className="inline-block px-3 py-1 rounded-full text-xs bg-indigo-500/20 text-indigo-300 border border-indigo-500/30">Filtering: {selectedCategory}</span>
            )}
          </div>
          
          <div className="space-y-4">
            <p className="text-slate-500 font-medium">How much time do you have?</p>
            <div className="grid grid-cols-3 gap-4">
              {['<15', '15-30', '30+'].map(time => {
                const colors = timeColors[time];
                return (
                  <button
                    key={time}
                    onClick={() => handleTimeSelect(time)}
                    className={`group relative p-6 rounded-3xl transition-all duration-200 hover:scale-105 active:scale-95 border shadow-lg hover:shadow-xl ${colors.bg} ${colors.border}`}
                  >
                    <Clock size={28} className={`mx-auto mb-2 ${colors.text}`} />
                    <span className={`block text-2xl font-bold ${colors.text}`}>{time}</span>
                    <span className="block text-sm text-slate-400">minutes</span>
                  </button>
                );
              })}
            </div>
          </div>

          <button onClick={() => setShowSettings(true)} className="mx-auto flex items-center gap-2 px-5 py-2.5 rounded-xl bg-slate-800/80 hover:bg-slate-700 border border-slate-700 transition-all text-slate-300 hover:text-white">
            <Sliders size={18} />
            <span className="font-medium">Customize</span>
          </button>
        </div>
      ) : currentTask ? (
        <div className="w-full max-w-md space-y-6">
          <div className="flex items-center justify-center gap-3">
            <div className={`p-2 rounded-xl ${categoryColors[currentTask.category].bg}`}>
              <CategoryIcon size={18} className="text-white" />
            </div>
            <span className={`font-medium ${categoryColors[currentTask.category].text}`}>{currentTask.category}</span>
            <span className="text-slate-600">•</span>
            <span className={`flex items-center gap-1 ${timeColors[currentTask.time].text}`}>
              <Clock size={14} />
              {getTimeLabel(currentTask.time)}
            </span>
          </div>

          <div className={`relative p-10 rounded-[2rem] text-center transition-all duration-500 ${
            justCompleted 
              ? 'bg-gradient-to-br from-emerald-500/30 to-emerald-600/20 border-2 border-emerald-400 shadow-2xl shadow-emerald-500/20' 
              : 'bg-gradient-to-br from-slate-800 via-slate-800 to-slate-900 border border-slate-700/50 shadow-2xl shadow-black/30'
          }`}>
            {!justCompleted && <div className="absolute inset-0 rounded-[2rem] bg-gradient-to-br from-indigo-500/10 via-transparent to-purple-500/10 pointer-events-none" />}
            {justCompleted && (
              <div className="mb-6 flex justify-center">
                <div className="w-16 h-16 rounded-full bg-emerald-500 flex items-center justify-center animate-bounce">
                  <Check className="text-white" size={32} />
                </div>
              </div>
            )}
            <h2 className="text-3xl font-bold mb-4 text-white leading-tight">{currentTask.name}</h2>
            {currentTask.childIds && <p className="text-sm text-indigo-400 mb-3 flex items-center justify-center gap-2"><Sparkles size={14} />Completing this marks sub-tasks done too!</p>}
            {currentTask.parentId && <p className="text-sm text-amber-400 mb-3">Part of: {tasks.find(t => t.id === currentTask.parentId)?.name}</p>}
            <div className="flex items-center justify-center gap-2 text-slate-400">
              <Clock size={14} />
              <span className="text-sm">Last done: {formatDate(currentTask.lastCompleted)}</span>
            </div>
            {justCompleted && <p className="text-emerald-300 mt-4 font-semibold">✓ Completed just now!</p>}
          </div>

          <div className="flex justify-center items-center gap-2">
            <div className="flex gap-1.5">
              {[1, 2, 3].map(i => (
                <div key={i} className={`w-3 h-3 rounded-full transition-all ${i <= currentTask.priority ? 'bg-indigo-500 shadow-sm shadow-indigo-500/50' : 'bg-slate-700'}`} />
              ))}
            </div>
            <span className="text-xs text-slate-500 ml-1">priority</span>
          </div>

          {!justCompleted ? (
            <div className="flex gap-4">
              <button onClick={handleReroll} className="flex-1 py-5 bg-slate-800 hover:bg-slate-700 rounded-2xl flex items-center justify-center gap-3 transition-all active:scale-95 border border-slate-700/50">
                <RotateCcw size={22} />
                <span className="font-medium text-lg">Reroll</span>
              </button>
              <button onClick={handleComplete} className="flex-1 py-5 bg-gradient-to-r from-indigo-500 to-indigo-600 hover:from-indigo-400 hover:to-indigo-500 rounded-2xl flex items-center justify-center gap-3 transition-all active:scale-95 shadow-lg shadow-indigo-500/30 font-bold text-lg">
                <Check size={22} />
                <span>I'll do it!</span>
              </button>
            </div>
          ) : (
            <div className="flex gap-4">
              <button onClick={() => { setCurrentTask(null); setSelectedTime(null); }} className="flex-1 py-5 bg-slate-800 hover:bg-slate-700 rounded-2xl transition-all active:scale-95 border border-slate-700/50 font-medium text-lg">Done for now</button>
              <button onClick={() => handleTimeSelect(selectedTime)} className="flex-1 py-5 bg-gradient-to-r from-indigo-500 to-purple-600 hover:from-indigo-400 hover:to-purple-500 rounded-2xl flex items-center justify-center gap-3 transition-all active:scale-95 shadow-lg shadow-indigo-500/30 font-bold text-lg">
                <Sparkles size={20} />
                One more!
              </button>
            </div>
          )}

          <button onClick={() => { setCurrentTask(null); setSelectedTime(null); }} className="w-full py-3 text-slate-500 hover:text-slate-300 transition-colors text-sm">← Pick different time</button>
        </div>
      ) : (
        <div className="text-center space-y-4">
          <p className="text-slate-400">No tasks found for this time & category.</p>
          <button onClick={() => { setCurrentTask(null); setSelectedTime(null); }} className="px-6 py-3 bg-slate-700 hover:bg-slate-600 rounded-xl transition">Go back</button>
        </div>
      )}
    </div>
  );

  const TasksTab = () => (
    <div className="flex-1 overflow-auto p-4 pb-24">
      <div className="max-w-lg mx-auto space-y-4">
        {categories.map(category => {
          const Icon = categoryIcons[category];
          const colors = categoryColors[category];
          const categoryTasks = tasks.filter(t => t.category === category && !t.parentId);
          const isExpanded = expandedCategories.includes(category);
          const isAddingHere = addingToCategory === category;
          const suggestions = isAddingHere ? getFilteredSuggestions(category) : [];
          
          return (
            <div key={category} className={`rounded-2xl border ${colors.border} bg-slate-900/50 overflow-hidden`}>
              <button onClick={() => toggleCategory(category)} className={`w-full px-4 py-4 flex items-center justify-between ${colors.light} hover:brightness-110 transition-all`}>
                <div className="flex items-center gap-3">
                  <div className={`p-2 rounded-xl ${colors.bg}`}><Icon size={18} className="text-white" /></div>
                  <span className="font-semibold text-white">{category}</span>
                  <span className="text-xs text-slate-500 bg-slate-800 px-2 py-0.5 rounded-full">{categoryTasks.length}</span>
                </div>
                {isExpanded ? <ChevronDown size={20} className="text-slate-400" /> : <ChevronRight size={20} className="text-slate-400" />}
              </button>
              
              {isExpanded && (
                <div className="divide-y divide-slate-800/50">
                  {categoryTasks.map(task => {
                    const subtaskProgress = getSubtaskProgress(task);
                    const hasSubtasks = task.childIds && task.childIds.length > 0;
                    const completed = isRecentlyCompleted(task);
                    const tColors = timeColors[task.time];
                    
                    return (
                      <div key={task.id} className="px-4 py-3 hover:bg-slate-800/30 transition-colors">
                        <div className="flex items-center gap-4">
                          {hasSubtasks ? (
                            <div className="relative flex items-center justify-center flex-shrink-0">
                              <ProgressCircle percentage={subtaskProgress || 0} size={40} />
                              <span className="absolute text-[10px] font-bold text-indigo-400">{subtaskProgress || 0}%</span>
                            </div>
                          ) : (
                            <TaskCircle isCompleted={completed} onClick={() => toggleTaskCompletion(task.id)} />
                          )}
                          <div className="flex-1 min-w-0">
                            <p className={`font-medium truncate ${completed ? 'text-slate-500 line-through' : 'text-white'}`}>{task.name}</p>
                            {hasSubtasks && <p className="text-xs text-slate-500">{task.childIds.length} subtasks</p>}
                          </div>
                          <div className="flex items-center gap-3 text-xs flex-shrink-0">
                            <span className={`px-2.5 py-1 rounded-lg border ${tColors.bg} ${tColors.text} ${tColors.border} font-medium`}>{getTimeLabel(task.time)}</span>
                            <span className="text-slate-500 w-16 text-right">{formatDate(task.lastCompleted)}</span>
                          </div>
                        </div>
                        
                        {hasSubtasks && (
                          <div className="ml-14 mt-3 space-y-2">
                            {tasks.filter(t => task.childIds.includes(t.id)).map(subtask => {
                              const subCompleted = isRecentlyCompleted(subtask);
                              const subTColors = timeColors[subtask.time];
                              return (
                                <div key={subtask.id} className="flex items-center gap-3">
                                  <TaskCircle isCompleted={subCompleted} onClick={() => toggleTaskCompletion(subtask.id)} size="small" />
                                  <span className={`flex-1 text-sm ${subCompleted ? 'line-through text-slate-500' : 'text-slate-300'}`}>{subtask.name}</span>
                                  <span className={`text-xs px-2 py-0.5 rounded ${subTColors.bg} ${subTColors.text}`}>{getTimeLabel(subtask.time)}</span>
                                </div>
                              );
                            })}
                          </div>
                        )}
                      </div>
                    );
                  })}
                  
                  {isAddingHere ? (
                    <div className="px-4 py-4 bg-slate-800/50">
                      <div className="space-y-3">
                        <AutocompleteInput
                          value={newTaskName}
                          onChange={setNewTaskName}
                          suggestions={suggestions}
                          onSubmit={() => handleAddTask(category)}
                          placeholder="Task name..."
                        />
                        
                        <div className="flex items-center gap-2">
                          <span className="text-xs text-slate-500">Time:</span>
                          {['<15', '15-30', '30+'].map(time => {
                            const tColors = timeColors[time];
                            return (
                              <button
                                key={time}
                                onClick={() => setNewTaskTime(time)}
                                className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all border ${
                                  newTaskTime === time ? `${tColors.bg} ${tColors.text} ${tColors.border}` : 'bg-slate-700 text-slate-400 border-slate-600 hover:bg-slate-600'
                                }`}
                              >
                                {time}
                              </button>
                            );
                          })}
                        </div>
                        
                        <div className="flex gap-2">
                          <button onClick={() => { setAddingToCategory(null); setNewTaskName(''); }} className="flex-1 py-2.5 bg-slate-700 hover:bg-slate-600 rounded-xl text-sm transition-colors">Cancel</button>
                          <button onClick={() => handleAddTask(category)} disabled={!newTaskName.trim()} className="flex-1 py-2.5 bg-indigo-500 hover:bg-indigo-400 disabled:bg-slate-700 disabled:text-slate-500 rounded-xl text-sm font-medium transition-colors">Add Task</button>
                        </div>
                      </div>
                    </div>
                  ) : (
                    <button onClick={() => { setAddingToCategory(category); setNewTaskName(''); setNewTaskTime('<15'); }} className="w-full px-4 py-3 flex items-center gap-2 text-slate-500 hover:text-indigo-400 hover:bg-slate-800/30 transition-colors">
                      <Plus size={18} />
                      <span className="text-sm">Add task</span>
                    </button>
                  )}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950 text-white flex flex-col relative">
      <div className="flex justify-center items-center p-4 border-b border-slate-800/50">
        <h1 className="text-xl font-bold tracking-tight bg-gradient-to-r from-white to-slate-400 bg-clip-text text-transparent">One Thing</h1>
      </div>

      {showSettings && (
        <div className="absolute inset-0 bg-black/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 rounded-3xl p-6 w-full max-w-sm space-y-6 border border-slate-800">
            <div className="flex justify-between items-center">
              <h2 className="text-lg font-semibold">Customize</h2>
              <button onClick={() => setShowSettings(false)} className="p-2 hover:bg-white/10 rounded-xl"><X size={20} /></button>
            </div>
            
            <div className="space-y-3">
              <div className="flex justify-between text-sm">
                <span className="text-slate-400 flex items-center gap-2"><Sparkles size={14} /> Priority</span>
                <span className="text-slate-400 flex items-center gap-2">Random <Dices size={14} /></span>
              </div>
              <input type="range" min="0" max="100" value={randomness} onChange={(e) => setRandomness(Number(e.target.value))} className="w-full accent-indigo-500 h-2 rounded-full" />
              <p className="text-xs text-slate-500 text-center">
                {randomness < 33 ? 'Focuses on overdue tasks' : randomness > 66 ? 'Mostly random picks' : 'Balanced selection'}
              </p>
            </div>

            <div className="space-y-3">
              <label className="text-sm text-slate-400">Roll from category</label>
              <div className="flex flex-wrap gap-2">
                {['All', ...categories].map(cat => (
                  <button key={cat} onClick={() => setSelectedCategory(cat)} className={`px-4 py-2 rounded-xl text-sm font-medium transition-all ${selectedCategory === cat ? 'bg-indigo-500 text-white shadow-lg shadow-indigo-500/30' : 'bg-slate-800 text-slate-300 hover:bg-slate-700'}`}>{cat}</button>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {activeTab === 'roll' ? <RollTab /> : <TasksTab />}

      <div className="fixed bottom-0 left-0 right-0 bg-slate-900/90 backdrop-blur-lg border-t border-slate-800/50">
        <div className="flex">
          <button onClick={() => setActiveTab('roll')} className={`flex-1 py-4 flex flex-col items-center gap-1 transition-all ${activeTab === 'roll' ? 'text-indigo-400' : 'text-slate-500 hover:text-slate-300'}`}>
            <Sparkles size={24} />
            <span className="text-xs font-medium">Roll</span>
          </button>
          <button onClick={() => setActiveTab('tasks')} className={`flex-1 py-4 flex flex-col items-center gap-1 transition-all ${activeTab === 'tasks' ? 'text-indigo-400' : 'text-slate-500 hover:text-slate-300'}`}>
            <List size={24} />
            <span className="text-xs font-medium">Tasks</span>
          </button>
        </div>
      </div>
    </div>
  );
}
