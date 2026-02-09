import { useState, useEffect, useRef } from "react";

interface AutocompleteInputProps {
  value: string;
  onChange: (value: string) => void;
  suggestions: string[];
  placeholder: string;
  onSubmit: () => void;
}

const AutocompleteInput = ({
  value,
  onChange,
  suggestions,
  placeholder,
  onSubmit,
}: AutocompleteInputProps) => {
  const [selectedIndex, setSelectedIndex] = useState(0);
  const inputRef = useRef<HTMLInputElement>(null);
  useEffect(() => {
    setSelectedIndex(0);
  }, [suggestions.length, value]);
  const currentSuggestion = suggestions[selectedIndex] || "";

  const getGhostText = () => {
    if (!currentSuggestion) return "";
    if (value.length === 0) return currentSuggestion;

    const lowerValue = value.toLowerCase();
    const lowerSuggestion = currentSuggestion.toLowerCase();

    if (lowerSuggestion.startsWith(lowerValue)) {
      return currentSuggestion.slice(value.length);
    }
    return "";
  };

  const ghostText = getGhostText();
  const showGhost =
    suggestions.length > 0 && (value.length === 0 || ghostText.length > 0);

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Tab" && showGhost && currentSuggestion) {
      e.preventDefault();
      onChange(currentSuggestion);
    } else if (e.key === "ArrowDown") {
      e.preventDefault();
      setSelectedIndex((prev) => (prev + 1) % Math.max(suggestions.length, 1));
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setSelectedIndex(
        (prev) =>
          (prev - 1 + suggestions.length) % Math.max(suggestions.length, 1),
      );
    } else if (e.key === "Enter" && value.trim()) {
      e.preventDefault();
      onSubmit();
    }
  };
  return (
    <div className="relative">
      <div className="absolute inset-0 px-4 py-3 pointer-events-none flex items-center">
        <span className="text-transparent">{value}</span>
        {showGhost && (
          <span className="text-slate-600">
            {value.length === 0 ? currentSuggestion : ghostText}
          </span>
        )}
      </div>

      <input
        ref={inputRef}
        type="text"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        onKeyDown={handleKeyDown}
        placeholder={!showGhost ? placeholder : ""}
        className="w-full px-4 py-3 bg-transparent border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500 relative z-10"
        autoFocus
      />

      {showGhost && suggestions.length > 0 && (
        <div className="absolute right-3 top-1/2 -translate-y-1/2 flex items-center gap-2 text-xs text-slate-500 pointer-events-none z-20">
          <span className="bg-slate-800 px-1.5 py-0.5 rounded text-[10px]">
            Tab
          </span>
          {suggestions.length > 1 && (
            <>
              <span className="bg-slate-800 px-1.5 py-0.5 rounded text-[10px]">
                ↑↓
              </span>
              <span className="text-slate-600">
                {selectedIndex + 1}/{suggestions.length}
              </span>
            </>
          )}
        </div>
      )}
    </div>
  );
};

export default AutocompleteInput;
