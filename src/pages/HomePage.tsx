import Header from "../header/Header";
import Companies from "../landingPage/Companies";
import DreamJob from "../landingPage/DreamJob";
import JobCategory from "../landingPage/JobCategory";

const HomePage = () => {
  return (
    <div className="min-h[100vh] bg-mine-shaft-950 font-['poppins']">
      <Header />
      <DreamJob/>
      <Companies/>
      <JobCategory/>
    </div>
  );
};

export default HomePage;
