import React from "react";
import MultiInput from "./MultiInput";
import { dropdownData } from "../Data/JobsData";

const Searchbar = () => {
  return (
    <div className="flex">
      {dropdownData.map((item, index) => (
        <div key={index} className="w-1/5">
          <MultiInput {...item} />
        </div>
      ))}
    </div>
  );
};

export default Searchbar;
