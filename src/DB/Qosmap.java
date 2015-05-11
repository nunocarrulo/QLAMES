/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nuno
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Qosmap.findAll", query = "SELECT q FROM Qosmap q"),
    @NamedQuery(name = "Qosmap.findById", query = "SELECT q FROM Qosmap q WHERE q.id = :id"),
    @NamedQuery(name = "Qosmap.findBySwID", query = "SELECT q FROM Qosmap q WHERE q.swID = :swID"),
    @NamedQuery(name = "Qosmap.findByPortID", query = "SELECT q FROM Qosmap q WHERE q.portID = :portID"),
    @NamedQuery(name = "Qosmap.findByPortUUID", query = "SELECT q FROM Qosmap q WHERE q.portUUID = :portUUID"),
    @NamedQuery(name = "Qosmap.findByQosUUID", query = "SELECT q FROM Qosmap q WHERE q.qosUUID = :qosUUID"),
    @NamedQuery(name = "Qosmap.findByQueueUUID", query = "SELECT q FROM Qosmap q WHERE q.queueUUID = :queueUUID")})
public class Qosmap implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    private String swID;
    @Basic(optional = false)
    private String portID;
    @Basic(optional = false)
    private String portUUID;
    @Basic(optional = false)
    private String qosUUID;
    @Basic(optional = false)
    private String queueUUID;
    @JoinColumn(name = "resID", referencedColumnName = "ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Reservation resID;

    public Qosmap() {
    }

    public Qosmap(Integer id) {
        this.id = id;
    }

    public Qosmap(String swID, String portID, String portUUID, String qosUUID, String queueUUID) {
        this.swID = swID;
        this.portID = portID;
        this.portUUID = portUUID;
        this.qosUUID = qosUUID;
        this.queueUUID = queueUUID;
    }
    
    public Qosmap(Integer id, String swID, String portID, String portUUID, String qosUUID, String queueUUID) {
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
        if (!(object instanceof Qosmap)) {
            return false;
        }
        Qosmap other = (Qosmap) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DB.Qosmap[ id=" + id + " ]";
    }
    
}
