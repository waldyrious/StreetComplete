package de.westnordost.streetcomplete.data.osm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;

import de.westnordost.osmapi.map.data.Element;
import de.westnordost.streetcomplete.data.osm.download.MapDataWithGeometryHandler;
import de.westnordost.streetcomplete.data.osm.download.OverpassMapDataDao;
import de.westnordost.streetcomplete.data.osm.tql.FiltersParser;
import de.westnordost.streetcomplete.data.osm.tql.TagFilterExpression;
import de.westnordost.osmapi.map.data.BoundingBox;

/** Quest type that simply makes a certain overpass query using tag filters and creates quests for
 *  every element received */
public abstract class SimpleOverpassQuestType extends AOsmElementQuestType
{
	private final OverpassMapDataDao overpassServer;

	private TagFilterExpression filter;

	public SimpleOverpassQuestType(OverpassMapDataDao overpassServer)
	{
		this.overpassServer = overpassServer;
		filter = new FiltersParser().parse(getTagFilters());
	}

	/** @return a query string that is accepted by Overpass and does not exceed the given bbox */
	String getOverpassQuery(BoundingBox bbox)
	{
		return filter.toOverpassQLString(bbox, true);
	}

	protected abstract String getTagFilters();

	public boolean download(BoundingBox bbox, MapDataWithGeometryHandler handler)
	{
		return overpassServer.getAndHandleQuota(getOverpassQuery(bbox), handler);
	}

	@Nullable @Override public Boolean isApplicableTo(Element element)
	{
		return filter.matches(element);
	}
}
