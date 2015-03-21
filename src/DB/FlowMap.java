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
    @NamedQuery(name = "FlowMap.findAll", query = "SELECT f FROM FlowMap f"),
    @NamedQuery(name = "FlowMap.findById", query = "SELECT f FROM FlowMap f WHERE f.id = :id"),
    @NamedQuery(name = "FlowMap.findBySwID", query = "SELECT f FROM FlowMap f WHERE f.swID = :swID"),
    @NamedQuery(name = "FlowMap.findByPortID", query = "SELECT f FROM FlowMap f WHERE f.portID = :portID"),
    @NamedQuery(name = "FlowMap.findByTableID", query = "SELECT f FROM FlowMap f WHERE f.tableID = :tableID"),
    @NamedQuery(name = "FlowMap.findByFlowID", query = "SELECT f FROM FlowMap f WHERE f.flowID = :flowID")})
public class FlowMap implements Serializable {
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
    private int tableID;
    @Basic(optional = false)
    private int flowID;
    @JoinColumn(name = "resID", referencedColumnName = "ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Reservation resID;

    public FlowMap() {
    }

    public FlowMap(Integer id) {
        this.id = id;
    }

    public FlowMap(String swID, String portID, int tableID, int flowID) {
        this.swID = swID;
        this.portID = portID;
        this.tableID = tableID;
        this.flowID = flowID;
    }
    
    public FlowMap(Integer id, String swID, String portID, int tableID, int flowID) {
        this.id = id;
        this.swID = swID;
        this.portID = portID;
        this.tableID = tableID;
        this.flowID = flowID;
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

    public int getTableID() {
        return tableID;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    public int getFlowID() {
        return flowID;
    }

    public void setFlowID(int flowID) {
        this.flowID = flowID;
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
        if (!(object instanceof FlowMap)) {
            return false;
        }
        FlowMap other = (FlowMap) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DB.FlowMap[ id=" + id + " ]";
    }
    
}
