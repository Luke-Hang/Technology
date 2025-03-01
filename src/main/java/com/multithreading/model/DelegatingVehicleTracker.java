package com.huawei.model;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiehang
 * @create 2022-09-26 11:57
 *
 * 委托车辆跟踪器
 */
public class DelegatingVehicleTracker {

    /**车辆坐标**/
    //将线程安全委托给ConcurrentHashMap
    private final ConcurrentHashMap<String, Point> locations;

    /**不可修改的地图**/
    private final Map<String, Point> unmodifiableMap;

    public DelegatingVehicleTracker(Map<String, Point> points) {

        locations = new ConcurrentHashMap<String, Point>(points);
        unmodifiableMap = Collections.unmodifiableMap(locations);
    }

    public Map<String, Point> getLocations(){
        return unmodifiableMap;

    }

    public Point getLocation(String id) {
        return locations.get(id);
    }

    public void setLocation(String id, int x, int y) {
        if (locations.replace(id, new Point(x, y)) == null) {
            throw new IllegalArgumentException("invalid vehicle name：" + id);
        }
    }
}
