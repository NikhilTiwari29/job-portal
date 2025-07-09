import { Divider } from "@mantine/core";
import Searchbar from "../findJobs/Searchbar";

const FindJobs = () => {
  return (
    <div className="min-h[100vh] bg-mine-shaft-950 font-['poppins']">
      <Divider size="xs" mx="md" />
      <Searchbar />
    </div>
  );
};

export default FindJobs;
