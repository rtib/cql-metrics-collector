/*
 * Copyright 2024-2025 T. Repasi <rtib@users.noreply.github.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.rtib.cmc.model.system;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import java.net.InetAddress;

/**
 * Entity class to hold query result of
 *   SELECT release_version, cluster_name, data_center, rack, listen_address, listen_port FROM system.local
 * 
 * This read-only entity class provides property getter methods of the legacy bean pattern as well, as
 * record style getter methods for forward compatibility with Java-14 records. Legacy getter methods are
 * annotated deprecated.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
@Entity
@CqlName("local")
@PropertyStrategy(mutable = false)
public final class SystemInfo {

    public SystemInfo(String release_version, String cluster_name, String data_center, String rack, InetAddress listen_address, int listen_port) {
        this.release_version = release_version;
        this.cluster_name = cluster_name;
        this.data_center = data_center;
        this.rack = rack;
        this.listen_address = listen_address;
        this.listen_port = listen_port;
    }

    private final String release_version;
    private final String cluster_name;
    private final String data_center;
    private final String rack;
    private final InetAddress listen_address;
    private final int listen_port;

    /**
     * Get the value of release_version
     *
     * @return the value of release_version
     */
    public String release_version() { return release_version; }

    /**
     * Get the value of cluster_name
     *
     * @return the value of cluster_name
     */
    public String cluster_name() { return cluster_name; }

    /**
     * Get the value of data_center
     *
     * @return the value of data_center
     */
    public String data_center() { return data_center; }

    /**
     * Get the value of rack
     *
     * @return the value of rack
     */
    public String rack() { return rack; }

    /**
     * Get the value of listen_address
     *
     * @return the value of listen_address
     */
    public InetAddress listen_address() { return listen_address; }
    
    /**
     * Get the value of listen_port
     *
     * @return the value of listen_port
     */
    public int listen_port() { return listen_port; }

    /**
     * Get the value of release_version
     *
     * @return the value of release_version
     * @deprecated
     */
    @Deprecated
    public String getRelease_version() { return release_version; }

    /**
     * Get the value of cluster_name
     *
     * @return the value of cluster_name
     * @deprecated
     */
    @Deprecated
    public String getCluster_name() { return cluster_name; }

    /**
     * Get the value of data_center
     *
     * @return the value of data_center
     * @deprecated
     */
    @Deprecated
    public String getData_center() { return data_center; }

    /**
     * Get the value of rack
     *
     * @return the value of rack
     * @deprecated
     */
    @Deprecated
    public String getRack() { return rack; }

    /**
     * Get the value of listen_address
     *
     * @return the value of listen_address
     * @deprecated
     */
    @Deprecated
    public InetAddress getListen_address() { return listen_address; }
    
    /**
     * Get the value of listen_port
     *
     * @return the value of listen_port
     * @deprecated
     */
    @Deprecated
    public int getListen_port() { return listen_port; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("localInfo [");
        sb.append("release_version=").append(release_version);
        sb.append(", cluster_name=").append(cluster_name);
        sb.append(", data_center=").append(data_center);
        sb.append(", rack=").append(rack);
        sb.append(", listen_address=").append(listen_address);
        sb.append(", listen_port=").append(listen_port);
        sb.append(']');
        return sb.toString();
    }
}
