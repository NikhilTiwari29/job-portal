import { Link, useLocation } from "react-router-dom";

const NavLinks = () => {
  const links = [
    { name: "Find Jobs", url: "/find-jobs" },
    { name: "Find Talent", url: "/find-talent" },
    { name: "Upload Job", url: "/upload-job" },
    { name: "About Us", url: "/about-us" },
  ];

  const location = useLocation();

  return (
    <div className="flex gap-5 h-full items-center text-mine-shaft-300">
      {links.map((link, index) => {
        const isActive = location.pathname === link.url;

        return (
          <Link
            key={index}
            to={link.url}
            className={`px-4 py-2 transition-all duration-200 ease-in-out rounded-md
    ${
      isActive
        ? "text-bright-sun-400 border-t-4 border-bright-sun-400"
        : "text-mine-shaft-300"
    }
    ${!isActive ? "hover:text-white hover:border hover:border-white" : ""}
  `}
          >
            {link.name}
          </Link>
        );
      })}
    </div>
  );
};

export default NavLinks;
