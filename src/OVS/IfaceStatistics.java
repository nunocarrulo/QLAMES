/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OVS;

/**
 *
 * @author nuno
 */
public class IfaceStatistics {
    
    
    private long collisions;

    private long rx_bytes;
    private long rx_packets;
    private long rx_dropped;
    private long rx_errors;
    
    private long lastTx_bytes;
    private long tx_bytes;
    private long tx_packets;
    private long tx_dropped;
    private long tx_errors;

    public IfaceStatistics() {
        this.collisions = 0;
        this.rx_bytes = 0;
        this.rx_packets = 0;
        this.rx_dropped = 0;
        this.rx_errors = 0;
        this.tx_bytes = 0;
        this.tx_packets = 0;
        this.tx_dropped = 0;
        this.tx_errors = 0;
        lastTx_bytes = 0;
    }

    public IfaceStatistics(long collisions, long rx_bytes, long rx_packets, long rx_dropped, long rx_errors, long tx_bytes, long tx_packets, long tx_dropped, long tx_errors) {
        this.collisions = collisions;
        this.rx_bytes = rx_bytes;
        this.rx_packets = rx_packets;
        this.rx_dropped = rx_dropped;
        this.rx_errors = rx_errors;
        this.tx_bytes = tx_bytes;
        this.tx_packets = tx_packets;
        this.tx_dropped = tx_dropped;
        this.tx_errors = tx_errors;
    }

    public void setIfaceStatistics(long collisions, long rx_bytes, long rx_packets, long rx_dropped, long rx_errors, long tx_bytes, long tx_packets, long tx_dropped, long tx_errors){
        long aux = this.tx_bytes;
        this.lastTx_bytes = aux;
        this.collisions = collisions;
        this.rx_bytes = rx_bytes;
        this.rx_packets = rx_packets;
        this.rx_dropped = rx_dropped;
        this.rx_errors = rx_errors;
        this.tx_bytes = tx_bytes;
        this.tx_packets = tx_packets;
        this.tx_dropped = tx_dropped;
        this.tx_errors = tx_errors;
    }
    public long getCollisions() {
        return collisions;
    }

    public void setCollisions(long collisions) {
        this.collisions = collisions;
    }

    public long getRx_bytes() {
        return rx_bytes;
    }

    public void setRx_bytes(long rx_bytes) {
        this.rx_bytes = rx_bytes;
    }

    public long getRx_packets() {
        return rx_packets;
    }

    public void setRx_packets(long rx_packets) {
        this.rx_packets = rx_packets;
    }

    public long getRx_dropped() {
        return rx_dropped;
    }

    public void setRx_dropped(long rx_dropped) {
        this.rx_dropped = rx_dropped;
    }

    public long getRx_errors() {
        return rx_errors;
    }

    public void setRx_errors(long rx_errors) {
        this.rx_errors = rx_errors;
    }

    public long getTx_bytes() {
        return tx_bytes;
    }

    public void setTx_bytes(long tx_bytes) {
        this.tx_bytes = tx_bytes;
    }

    public long getTx_packets() {
        return tx_packets;
    }

    public void setTx_packets(long tx_packets) {
        this.tx_packets = tx_packets;
    }

    public long getTx_dropped() {
        return tx_dropped;
    }

    public void setTx_dropped(long tx_dropped) {
        this.tx_dropped = tx_dropped;
    }

    public long getTx_errors() {
        return tx_errors;
    }

    public void setTx_errors(long tx_errors) {
        this.tx_errors = tx_errors;
    }

    public long getLastTx_bytes() {
        return lastTx_bytes;
    }

    public void setLastTx_bytes(long lastTx_bytes) {
        this.lastTx_bytes = lastTx_bytes;
    }

    @Override
    public String toString() {
        return "IfaceStatistics{" + "collisions=" + collisions + ", rx_bytes=" + rx_bytes + ", rx_packets=" + rx_packets + ", rx_dropped=" + rx_dropped + ", rx_errors=" + rx_errors + ", tx_bytes=" + tx_bytes + ", tx_packets=" + tx_packets + ", tx_dropped=" + tx_dropped + ", tx_errors=" + tx_errors + '}';
    }
    
}
