"""Custom topology example

Two directly connected switches plus a host for each switch:

   host --- switch --- switch --- host

Adding the 'topos' dict with a key/value pair to generate our newly defined
topology enables one to pass in '--topo=mytopo' from the command line.
"""

from mininet.topo import Topo

class MyTopo( Topo ):
    "Simple topology example."

    def __init__( self ):
        "Create custom topo."

        # Initialize topology
        Topo.__init__( self )

        # Add hosts and switches
        hostA1 = self.addHost( 'h1', ip='10.0.0.1', mask='255.255.255.0' ) #A
        hostA2 = self.addHost( 'h2', ip='10.0.0.2', mask='255.255.255.0' ) #A
        hostA3 = self.addHost( 'h3', ip='10.0.0.3', mask='255.255.255.0' ) #A
        hostB1 = self.addHost( 'h4', ip='10.0.0.11', mask='255.255.255.0' ) #B
        hostB2 = self.addHost( 'h5', ip='10.0.0.12', mask='255.255.255.0' ) #B
        hostB3 = self.addHost( 'h6', ip='10.0.0.13', mask='255.255.255.0' ) #B
        hostC1 = self.addHost( 'h7', ip='10.0.0.21', mask='255.255.255.0' ) #C
        hostC2 = self.addHost( 'h8', ip='10.0.0.22', mask='255.255.255.0' ) #C
        hostC3 = self.addHost( 'h9', ip='10.0.0.23', mask='255.255.255.0' ) #C
        hostD1 = self.addHost( 'h10', ip='10.0.0.31', mask='255.255.255.0' ) #D
        hostD2 = self.addHost( 'h11', ip='10.0.0.32', mask='255.255.255.0' ) #D
        hostD3 = self.addHost( 'h12', ip='10.0.0.33', mask='255.255.255.0' ) #D
        hostE1 = self.addHost( 'h13', ip='10.0.0.41', mask='255.255.255.0' ) #E
        hostE2 = self.addHost( 'h14', ip='10.0.0.42', mask='255.255.255.0' ) #E
        hostE3 = self.addHost( 'h15', ip='10.0.0.43', mask='255.255.255.0' ) #E


        # Switch Level 1
        switch1 = self.addSwitch( 's1' )#dummy cause mininet its stupid
        switch10 = self.addSwitch( 's10' )
        # Switch Level 2
        switch2 = self.addSwitch( 's2' )
        switch3 = self.addSwitch( 's3' )
        switch4 = self.addSwitch( 's4' )
        # Switch Level 3
        switch5 = self.addSwitch( 's5' )
        switch6 = self.addSwitch( 's6' )
        # Switch Level 4
        switch7 = self.addSwitch( 's7' )
        
        # Add links
        #Level 1
        self.addLink( switch10, switch2 )
        self.addLink( switch10, switch3 )
        self.addLink( switch10, switch4 )
        #Level 2
        #self.addLink( switch2, host1 )
        self.addLink( switch3, switch5 )
        self.addLink( switch3, switch6 )
        #self.addLink( switch4, host5 )
        #Level 3
        #self.addLink( switch5, host2 )
        self.addLink( switch5, switch7 )
        #self.addLink( switch6, host4 )
        self.addLink( switch6, switch7 )
        #Level 4
        #self.addLink( switch7, host3 )
        self.addLink( switch2, hostA1 )
        self.addLink( switch2, hostA2 )
        self.addLink( switch2, hostA3 )
        self.addLink( switch5, hostB1 )
        self.addLink( switch5, hostB2 )
        self.addLink( switch5, hostB3 )
        self.addLink( switch7, hostC1 )
        self.addLink( switch7, hostC2 )
        self.addLink( switch7, hostC3 )
        self.addLink( switch6, hostD1 )
        self.addLink( switch6, hostD2 )
        self.addLink( switch6, hostD3 )
        self.addLink( switch4, hostE1 )
        self.addLink( switch4, hostE2 )
        self.addLink( switch4, hostE3 )


topos = { 'mytopo': ( lambda: MyTopo() ) }