/*
 *  @author Philip Stutz
 *  @author Francisco de Freitas
 *  
 *  Copyright 2010 University of Zurich
 *      
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *         http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package com.signalcollect.interfaces

case class Request[ProxiedClass](command: ProxiedClass => Any, returnResult: Boolean = false)

case class Heartbeat(maySignal: Boolean)

// Some edge ids that get sent around will be incomplete, by having one or both ids set to 'null'.
case class EdgeId[Id](val sourceId: Id, val targetId: Id) {
  def withTargetId(t: Id): EdgeId[Id] = EdgeId(sourceId, t)
  def withSourceId(s: Id): EdgeId[Id] = EdgeId(s, targetId)
  def removeTargetId: EdgeId[Id] = EdgeId(sourceId, null.asInstanceOf[Id])
  def removeSourceId: EdgeId[Id] = EdgeId(null.asInstanceOf[Id], targetId)
  override def toString(): String = sourceId.toString + targetId.toString
}

case class BulkSignal[@specialized(Int, Long) Id, @specialized(Int, Long, Float, Double) Signal](val signals: Array[Signal], val targetIds: Array[Id], val sourceIds: Array[Id])

case class SignalMessage[@specialized(Int, Long) Id, @specialized(Int, Long, Float, Double) Signal](val targetId: Id, val sourceId: Option[Id], val signal: Signal)

// Convergence/pause detection
case class WorkerStatus(
  workerId: Int,
  isIdle: Boolean,
  isPaused: Boolean,
  messagesSent: Array[Long],
  messagesReceived: Long,
  workerStatistics: WorkerStatistics,
  systemInformation: SystemInformation)

case class WorkerStatistics(
  messagesSent: Array[Long],
  workerId: Int = -1,
  messagesReceived: Long = 0l,
  toSignalSize: Long = 0l,
  toCollectSize: Long = 0l,
  collectOperationsExecuted: Long = 0l,
  signalOperationsExecuted: Long = 0l,
  numberOfVertices: Long = 0l,
  verticesAdded: Long = 0l,
  verticesRemoved: Long = 0l,
  numberOfOutgoingEdges: Long = 0l,
  outgoingEdgesAdded: Long = 0l,
  outgoingEdgesRemoved: Long = 0l,
  receiveTimeoutMessagesReceived: Long = 0l,
  heartbeatMessagesReceived: Long = 0l,
  signalMessagesReceived: Long = 0l,
  bulkSignalMessagesReceived: Long = 0l,
  continueMessagesReceived: Long = 0l,
  requestMessagesReceived: Long = 0l,
  otherMessagesReceived: Long = 0l) {
  def +(other: WorkerStatistics): WorkerStatistics = {
    WorkerStatistics(
      { // Merges the sent messages arrays.
        if (messagesSent == null) other.messagesSent
        else if (other.messagesSent == null) messagesSent
        else if (messagesSent == null && other.messagesSent == null) null
        else {
          val merged = new Array[Long](messagesSent.length)
          var i = 0
          while (i < messagesSent.length) {
            merged(i) = messagesSent(i) + other.messagesSent(i)
            i += 1
          }
          merged
        }
      },
      -1,
      messagesReceived + other.messagesReceived,
      toSignalSize + other.toSignalSize,
      toCollectSize + other.toCollectSize,
      collectOperationsExecuted + other.collectOperationsExecuted,
      signalOperationsExecuted + other.signalOperationsExecuted,
      numberOfVertices + other.numberOfVertices,
      verticesAdded + other.verticesAdded,
      verticesRemoved + other.verticesRemoved,
      numberOfOutgoingEdges + other.numberOfOutgoingEdges,
      outgoingEdgesAdded + other.outgoingEdgesAdded,
      outgoingEdgesRemoved + other.outgoingEdgesRemoved,
      receiveTimeoutMessagesReceived + other.receiveTimeoutMessagesReceived,
      heartbeatMessagesReceived + other.heartbeatMessagesReceived,
      signalMessagesReceived + other.signalMessagesReceived,
      bulkSignalMessagesReceived + other.bulkSignalMessagesReceived,
      continueMessagesReceived + other.continueMessagesReceived,
      requestMessagesReceived + other.requestMessagesReceived,
      otherMessagesReceived + other.otherMessagesReceived)
  }
}

case class SystemInformation(
    workerId: Int = -1,
    os: String = "",
    runtime_mem_total: Long = 0l,
    runtime_mem_max: Long = 0l,
    runtime_mem_free: Long = 0l,
    runtime_cores: Long = 0l,
    jmx_committed_vms: Long = 0l,
    jmx_mem_free: Long = 0l,
    jmx_mem_total: Long = 0l,
    jmx_swap_free: Long = 0l,
    jmx_swap_total: Long = 0l,
    jmx_process_load: Double = 0.0,
    jmx_process_time: Double = 0.0,
    jmx_system_load: Double = 0.0
) {
  def +(other: SystemInformation): SystemInformation = {
    SystemInformation(
        -1,
        os + other.os,
        runtime_mem_total + other.runtime_mem_total,
        runtime_mem_max + other.runtime_mem_max,
        runtime_mem_free + other.runtime_mem_free,
        runtime_cores + other.runtime_cores,
        jmx_committed_vms + other.jmx_committed_vms,
        jmx_mem_free + other.jmx_mem_free,
        jmx_mem_total + other.jmx_mem_total,
        jmx_swap_free + other.jmx_swap_free,
        jmx_swap_total + other.jmx_swap_total,
        (jmx_process_load + other.jmx_process_load) / 2,
        jmx_process_time + other.jmx_process_time,
        (jmx_system_load + other.jmx_system_load) / 2)
  }
}

sealed trait LogMessage {
  def msg: Any
  def from: Any
}

case class Debug(msg: Any, from: Any) extends LogMessage
case class Config(msg: Any, from: Any) extends LogMessage
case class Info(msg: Any, from: Any) extends LogMessage
case class Warning(msg: Any, from: Any) extends LogMessage
case class Severe(msg: Any, from: Any) extends LogMessage
