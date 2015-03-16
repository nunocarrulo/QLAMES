/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nuno
 */
@Entity
@Table(name = "QosMap")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "QosMap.findAll", query = "SELECT q FROM QosMap q"),
    @NamedQuery(name = "QosMap.findById", query = "SELECT q FROM QosMap q WHERE q.id = :id"),
    @NamedQuery(name = "QosMap.findBySwID", query = "SELECT q FROM QosMap q WHERE q.swID = :swID"),
    @NamedQuery(name = "QosMap.findByPortID", query = "SELECT q FROM QosMap q WHERE q.portID = :portID"),
    @NamedQuery(name = "QosMap.findByPortUUID", query = "SELECT q FROM QosMap q WHERE q.portUUID = :portUUID"),
    @NamedQuery(name = "QosMap.findByQosUUID", query = "SELECT q FROM QosMap q WHERE q.qosUUID = :qosUUID"),
    @NamedQuery(name = "QosMap.findByQueueUUID", query = "SELECT q FROM QosMap q WHERE q.queueUUID = :queueUUID")})
public class QosMap implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "swID")
    private String swID;
    @Basic(optional = false)
    @Column(name = "portID")
    private String portID;
    @Basic(optional = false)
    @Column(name = "portUUID")
    private String portUUID;
    @Basic(optional = false)
    @Column(name = "qosUUID")
    private String qosUUID;
    @Basic(optional = false)
    @Column(name = "queueUUID")
    private String queueUUID;
    @JoinColumn(name = "resID", referencedColumnName = "ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Reservation resID;

    public QosMap() {
    }

    public QosMap(Integer id) {
        this.id = id;
    }

    public QosMap(Integer id, String swID, String portID, String portUUID, String qosUUID, String queueUUID) {
        this.id = id;
        this.swID = swID;
        this.portID = portID;
        this.portUUID = portUUID;
        this.qosUUID = qosUUID;
        this.queueUUID = queueUUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSwID() {
        return swID;
    }

    public void setSwID(String swID) {
        this.swID = swID;
    }

    public String getPortID() {
        return portID;
    }

    public void setPortID(String portID) {
        this.portID = portID;
    }

    public String getPortUUID() {
        return portUUID;
    }

    public void setPortUUID(String portUUID) {
        this.portUUID = portUUID;
    }

    public String getQosUUID() {
        return qosUUID;
    }

    public void setQosUUID(String qosUUID) {
        this.qosUUID = qosUUID;
    }

    public String getQueueUUID() {
        return queueUUID;
    }

    public void setQueueUUID(String queueUUID) {
        this.queueUUID = queueUUID;
    }

    public Reservation getResID() {
        return resID;
    }

    public void setResID(Reservation resID) {
        this.resID = resID;
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
        if (!(object instanceof QosMap)) {
            return false;
        }
        QosMap other = (QosMap) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "test.QosMap[ id=" + id + " ]";
    }
    
}
