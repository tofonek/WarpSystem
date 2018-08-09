package de.codingair.warpsystem.spigot.features.signs;

import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.gui.affiliations.Warp;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WarpSign {
    private Location location;
    private Warp warp;

    public WarpSign(Location location, Warp warp) {
        this.location = location;
        this.warp = warp;
    }

    public Location getLocation() {
        return location;
    }

    public Warp getWarp() {
        return warp;
    }

    public String toJSONString() {
        JSONObject json = new JSONObject();

        json.put("Loc", this.location.toJSONString(0));
        json.put("Warp", this.warp.getName());
        json.put("Category", this.warp.getCategory() == null ? null : this.warp.getCategory().getName());

        return json.toJSONString();
    }

    public static WarpSign fromJSONString(String s) {
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(s);

            Location loc = Location.getByJSONString((String) json.get("Loc"));
            Warp warp = WarpSystem.getInstance().getIconManager().getWarp((String) json.get("Warp"), json.get("Category") == null ? null : WarpSystem.getInstance().getIconManager().getCategory((String) json.get("Category")));

            return new WarpSign(loc, warp);
        } catch(ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}