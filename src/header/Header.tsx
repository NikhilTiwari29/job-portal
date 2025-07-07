import { Avatar, Indicator } from "@mantine/core";
import { IconAsset, IconBell, IconSettings } from "@tabler/icons-react";
import NavLinks from "./NavLinks";
const Header = () => {
  return (
    <div className="w-full bg-mine-shaft-950 h-20 text-white flex justify-between px-6 items-center">
      <div>
        <div className="flex gap-1 items-center text-bright-sun-400">
          <IconAsset className="h-9 w-9 stroke={2.5}" />
          <div className="text-3xl font-semibold">HuntJobs</div>
        </div>
      </div>
      {NavLinks()}
      <div className="flex gap-3 items-center">
        <div className="flex gap-2 items-center">
          <div>Nikhil</div>
          <Avatar src="avatar-9.png" alt="it's me" />
        </div>
        <div className="bg-mine-shaft-900 rounded-full px-1.5">
          <IconSettings stroke={1.5} />
        </div>
        <div className="bg-mine-shaft-900 rounded-full px-1.5">
          <Indicator
            color="brightSun.5"
            size={8}
            offset={6}
            withBorder
            processing
          >
            <IconBell stroke={1.5} />
          </Indicator>
        </div>
      </div>
    </div>
  );
};

export default Header;
