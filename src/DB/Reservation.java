/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author nuno
 */
@Entity
@Table(name = "Reservation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reservation.findAll", query = "SELECT r FROM Reservation r"),
    @NamedQuery(name = "Reservation.findById", query = "SELECT r FROM Reservation r WHERE r.id = :id"),
    @NamedQuery(name = "Reservation.findBySrcIP", query = "SELECT r FROM Reservation r WHERE r.srcIP = :srcIP"),
    @NamedQuery(name = "Reservation.findByDstIP", query = "SELECT r FROM Reservation r WHERE r.dstIP = :dstIP"),
    @NamedQuery(name = "Reservation.findByPriority", query = "SELECT r FROM Reservation r WHERE r.priority = :priority"),
    @NamedQuery(name = "Reservation.findByMinBW", query = "SELECT r FROM Reservation r WHERE r.minBW = :minBW"),
    @NamedQuery(name = "Reservation.findByMaxBW", query = "SELECT r FROM Reservation r WHERE r.maxBW = :maxBW"),
    @NamedQuery(name = "Reservation.findByStartDate", query = "SELECT r FROM Reservation r WHERE r.startDate = :startDate"),
    @NamedQuery(name = "Reservation.findByEndDate", query = "SELECT r FROM Reservation r WHERE r.endDate = :endDate")})
public class Reservation implements Serializable {
    @Basic(optional = false)
    private boolean applied;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "srcIP")
    private String srcIP;
    @Basic(optional = false)
    @Column(name = "dstIP")
    private String dstIP;
    @Basic(optional = false)
    @Column(name = "priority")
    private int priority;
    @Basic(optional = false)
    @Column(name = "minBW")
    private int minBW;
    @Column(name = "maxBW")
    private int maxBW;
    @Basic(optional = false)
    @Column(name = "startDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Basic(optional = false)
    @Column(name = "endDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resID", fetch = FetchType.LAZY)
    private Collection<QosMap> qosMapCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resID", fetch = FetchType.LAZY)
    private Collection<FlowMap> flowMapCollection;

    public Reservation() {
    }

    public Reservation(Integer id) {
        this.id = id;
    }

    public Reservation(String srcIP, String dstIP, int priority, int minBW, Date startDate, Date endDate) {
        this.srcIP = srcIP;
        this.dstIP = dstIP;
        this.priority = priority;
        this.minBW = minBW;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public Reservation(String srcIP, String dstIP, int priority, int minBW, int maxBW, Date startDate, Date endDate) {
        this.srcIP = srcIP;
        this.dstIP = dstIP;
        this.priority = priority;
        this.minBW = minBW;
        this.maxBW = maxBW;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public Reservation(Integer id, String srcIP, String dstIP, int priority, int minBW, Date startDate, Date endDate) {
        this.id = id;
        this.srcIP = srcIP;
        this.dstIP = dstIP;
        this.priority = priority;
        this.minBW = minBW;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSrcIP() {
        return srcIP;
    }

    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP;
    }

    public String getDstIP() {
        return dstIP;
    }

    public void setDstIP(String dstIP) {
        this.dstIP = dstIP;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getMinBW() {
        return minBW;
    }

    public void setMinBW(int minBW) {
        this.minBW = minBW;
    }

    public int getMaxBW() {
        return maxBW;
    }

    public void setMaxBW(int maxBW) {
        this.maxBW = maxBW;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @XmlTransient
    public Collection<QosMap> getQosMapCollection() {
        return qosMapCollection;
    }

    public void setQosMapCollection(Collection<QosMap> qosMapCollection) {
        this.qosMapCollection = qosMapCollection;
    }

    @XmlTransient
    public Collection<FlowMap> getFlowMapCollection() {
        return flowMapCollection;
    }

    public void setFlowMapCollection(Collection<FlowMap> flowMapCollection) {
        this.flowMapCollection = flowMapCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "test.Reservation[ id=" + id + " ]";
    }

    public boolean getApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }
    
}
