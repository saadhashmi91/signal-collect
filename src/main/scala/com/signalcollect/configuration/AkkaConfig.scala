package com.signalcollect.configuration

import com.typesafe.config._

object AkkaConfig {
  lazy val get = ConfigFactory.parseString(distributedConfig).withFallback(ConfigFactory.load).resolve
  val distributedConfig = """
akka {
  #logConfigOnStart=on
  #loglevel = DEBUG
  actor {
    #serialize-messages = on
    provider = "akka.remote.RemoteActorRefProvider"
    
  	pinned-dispatcher {
	  type = PinnedDispatcher
	  executor = "thread-pool-executor"
  	}

    serializers {
      java = "akka.serialization.JavaSerializer"
    }

    deployment {

      default {

        # if this is set to a valid remote address, the named actor will be deployed
        # at that node e.g. "akka://sys@host:port"
        remote = ""

        target {

          # A list of hostnames and ports for instantiating the children of a
          # non-direct router
          #   The format should be on "akka://sys@host:port", where:
          #    - sys is the remote actor system name
          #    - hostname can be either hostname or IP address the remote actor
          #      should connect to
          #    - port should be the port for the remote server on the other node
          # The number of actor instances to be spawned is still taken from the
          # nr-of-instances setting as for local routers; the instances will be
          # distributed round-robin among the given nodes.
          nodes = []

        }
      }
    }
  }

  remote {

    # Which implementation of akka.remote.RemoteTransport to use
    # default is a TCP-based remote transport based on Netty
    transport = "akka.remote.netty.NettyRemoteTransport"

    # Enable untrusted mode for full security of server managed actors, allows
    # untrusted clients to connect.
    untrusted-mode = off

    # Timeout for ACK of cluster operations, lik checking actor out etc.
    remote-daemon-ack-timeout = 30s

    # If this is "on", Akka will log all inbound messages at DEBUG level, if off then they are not logged
    log-received-messages = off

    # If this is "on", Akka will log all outbound messages at DEBUG level, if off then they are not logged
    log-sent-messages = off

    # Each property is annotated with (I) or (O) or (I&O), where I stands for “inbound” and O for “outbound” connections.
    # The NettyRemoteTransport always starts the server role to allow inbound connections, and it starts
    # active client connections whenever sending to a destination which is not yet connected; if configured
    # it reuses inbound connections for replies, which is called a passive client connection (i.e. from server
    # to client).
    netty {

      # (O) In case of increased latency / overflow how long
      # should we wait (blocking the sender) until we deem the send to be cancelled?
      # 0 means "never backoff", any positive number will indicate time to block at most.
      backoff-timeout = 0ms

      # (I&O) Generate your own with '$AKKA_HOME/scripts/generate_config_with_secure_cookie.sh'
      #     or using 'akka.util.Crypt.generateSecureCookie'
      secure-cookie = ""

      # (I) Should the remote server require that it peers share the same secure-cookie
      # (defined in the 'remote' section)?
      require-cookie = off

      # (I) Reuse inbound connections for outbound messages
      use-passive-connections = off

      # (I) The hostname or ip to bind the remoting to,
      # InetAddress.getLocalHost.getHostAddress is used if empty
      hostname = ""

      # (I) The default remote server port clients should connect to.
      # Default is 2552 (AKKA), use 0 if you want a random available port
      port = 0

      # (O) The address of a local network interface (IP Address) to bind to when creating
      # outbound connections. Set to "" or "auto" for automatic selection of local address.
      outbound-local-address = "auto"

      # (I&O) Increase this if you want to be able to send messages with large payloads
      message-frame-size = 1 MiB

      # (O) Timeout duration
      connection-timeout = 120s

      # (I) Sets the size of the connection backlog
      backlog = 8192

      # (I) Length in akka.time-unit how long core threads will be kept alive if idling
      execution-pool-keepalive = 60s

      # (I) Size of the core pool of the remote execution unit
      execution-pool-size = 4

      # (I) Maximum channel size, 0 for off
      max-channel-memory-size = 0b

      # (I) Maximum total size of all channels, 0 for off
      max-total-memory-size = 0b

      # (O) Time between reconnect attempts for active clients
      reconnect-delay = 5s

      # (O) Read inactivity period (lowest resolution is seconds)
      # after which active client connection is shutdown;
      # will be re-established in case of new communication requests.
      # A value of 0 will turn this feature off
      read-timeout = 0s

      # (O) Write inactivity period (lowest resolution is seconds)
      # after which a heartbeat is sent across the wire.
      # A value of 0 will turn this feature off
      write-timeout = 10s

      # (O) Inactivity period of both reads and writes (lowest resolution is seconds)
      # after which active client connection is shutdown;
      # will be re-established in case of new communication requests
      # A value of 0 will turn this feature off
      all-timeout = 0s

      # (O) Maximum time window that a client should try to reconnect for
      reconnection-time-window = 600s
    }

    # The dispatcher used for the system actor "network-event-sender"
    network-event-sender-dispatcher {
      executor = thread-pool-executor
      type = PinnedDispatcher
    }
  }
}
"""
}