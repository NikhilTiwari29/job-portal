import Footer from "../footer/Footer";
import Header from "../header/Header";
import Companies from "../landingPage/Companies";
import DreamJob from "../landingPage/DreamJob";
import JobCategory from "../landingPage/JobCategory";
import Subscribe from "../landingPage/Subscribe";
import Testimonials from "../landingPage/Testimonials";
import Working from "../landingPage/Working";

const HomePage = () => {
  return (
    <div className="min-h[100vh] bg-mine-shaft-950 font-['poppins']">
      <DreamJob />
      <Companies />
      <JobCategory />
      <Working />
      <Testimonials />
      <Subscribe />
    </div>
  );
};

export default HomePage;
