import { Avatar, Indicator, NavLink } from "@mantine/core";
import { IconAnchor, IconBell, IconSettings } from "@tabler/icons-react";
import Navlink from "./Navlink";

const Header = () => {
  return (
    <div className="w-full bg-mine-shaft-950 h-20 px-6 text-white flex justify-between items-center">
      <div className="flex gap-1 items-center text-bright-sun-400">
        <IconAnchor className="h-8 w-8" stroke={2.5} />
        <div className="text-3xl font-semibold">JobHook</div>
      </div>
     {Navlink()}
      <div className="flex gap-5 items-center">
        <div className="flex gap-2 items-center">
          <div>Nikhil</div>
          <Avatar src="avatar.png" alt="It's me" className="w-8 h-8" />
        </div>
        <div className="bg-mine-shaft-900 p-1.5 rounded-full ">
          <IconSettings stroke={1.5} />
        </div>
        <div className="bg-mine-shaft-900 p-1.5 rounded-full ">
          <Indicator color="brightSun.4" offset={6} size={9} processing>
            <IconBell stroke={1.5} />
          </Indicator>
        </div>
      </div>
    </div>
  );
};

export default Header;
