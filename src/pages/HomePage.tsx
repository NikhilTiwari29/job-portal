import Header from "../header/Header";
import Companies from "../landingPage/Companies";
import DreamJob from "../landingPage/DreamJob";

const HomePage = () => {
  return (
    <div className="min-h-[100vh] bg-mine-shaft-950 font-['poppins']">
      <Header />
      <DreamJob />
      <Companies /> 
    </div>
  );
};

export default HomePage;
