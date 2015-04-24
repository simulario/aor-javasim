<p><em>AOR-JavaSim</em> is part of the <em><strong>ER/AOR Simulation</strong></em> framework, which provides a language and tools for (basic and agent-based) <em><strong>discrete event simulation</strong></em>. <em>AOR-JavaSim</em> is a Java-based simulation management system implementing the abstract simulator defined by <em>ER/AOR Simulation</em>. It requires the installation of a Java 6 Development Kit (JDK 6) from the <a href='http://java.sun.com/javase/downloads/index.jsp'>Java SE download site</a>.</p>

<img src='http://hydrogen.informatik.tu-cottbus.de/talks/AORS-Tutorial/code-generation.png' alt='code generation' />

<p>For creating AOR simulation scenarios that run in the Web browser, visit the <a href='http://portal.simulario.de'>Simurena Development Portal</a>, which offers a free personal account for developers. The Simurena site also hosts a <a href='http://portal.simulario.de/public'>Public Library</a> of simulations and games.</p>

<p>The most distinctive feature of the <em>ER/AOR Simulation</em> framework are its high-level rule-based simulation languages <a href='http://oxygen.informatik.tu-cottbus.de/aors/ERSL.html'>ERSL</a> and <a href='http://oxygen.informatik.tu-cottbus.de/aors/AORSL.html'>AORSL</a>, which allow to define declarative simulation models that can be executed with the help of simulators implemented in different programming languages (Java, JavaScript, C++, etc.). The operational semantics of <em>AORSL</em> is defined by an abstract simulator architecture and execution model.</p>

<p>The <em>ER/AOR Simulation</em> framework is intended to be used as a general purpose simulation framework in science, engineering, education and entertainment.</p>

<ul>
<li>Most parts of the framework are <em>open-source-licensed</em></li>
<li>Both the behavior of the environment (its causality laws) and the behavior of agents are modeled with the help of <em>rules</em>, thus supporting high-level declarative modeling</li>
<li>Declarative <em>visualization</em> of objects and agents with the help of <em>view</em> definitions</li>
<li>Declarative <em>sonification</em> of events (MIDI/MP3 sound attachment) with <em>event appearance</em> definitions</li>
<li>AOR Simulation is the first open-source agent-based discrete event simulation framework that supports <em>cognitive agents</em> with a full-fledged model of <em>beliefs</em></li>
</ul>

<p>For more information please check out our <a href='http://code.google.com/p/aor-javasim/w/list'>Wiki pages</a> or visit the <a href='http://oxygen.informatik.tu-cottbus.de/aor/'>project homepage</a>.</p>

<table cellpadding='3' cellspacing='1' align='center' border='1'>
<thead>
<tr>
<th>Comparison Table</th><th><a href='http://repast.sourceforge.net/'>RePast</a></th><th><a href='http://ccl.northwestern.edu/netlogo/'>NetLogo</a></th><th><a href='http://www.agentisolutions.com/index.htm'>Brahms</a></th><th>AOR Simulation</th>
</tr>
</thead>
<tbody align='center'>
<tr>
<td align='left'>Distinguish between objects and agents</td><td>O</td><td>V</td><td>V</td><td>V</td>
</tr>
<tr>
<td align='left'>Provide a choice of space models</td><td>V</td><td>O</td><td>O</td><td>V</td>
</tr>
<tr>
<td align='left'>Based on a foundational ontology of events</td><td>O</td><td>O</td><td>O</td><td>V</td>
</tr>
<tr>
<td align='left'>Support physical objects/agents</td><td>O</td><td>O</td><td>O</td><td>V</td>
</tr>
<tr>
<td align='left'>Support a concept of activities</td><td>O</td><td>O</td><td>V</td><td>V</td>
</tr>
<tr>
<td align='left'>Rule-based behavior modeling</td><td>O</td><td>O</td><td>V</td><td>V</td>
</tr>
<tr>
<td align='left'>Cognitive model of perception</td><td>O</td><td>O</td><td>V</td><td>V</td>
</tr>
<tr>
<td align='left'>Distinguish between facts and beliefs</td><td>O</td><td>O</td><td>V</td><td>V</td>
</tr>
<tr>
<td align='left'>Support beliefs about other entities (belief triples)</td><td>O</td><td>O</td><td>V</td><td>V</td>
</tr>
</tbody>
</table>