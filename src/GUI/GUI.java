package GUI;

import java.awt.Container;

import javax.swing.JFrame;

import util.Arch;

public class GUI {
	private DynamicTableGUI _estacaoReserva, _registradores, _bufferReord, _memory, _status;
	//private ArchTomasulo _arch;
	public GUI() {
		int xPos = 0;
		int yPos = 0;
		//_arch = arch;
		String[] estacaoReservaTitle = new String[] {"ID", "Tipo", "Busy", "Instrução", "Vj", "Vk", "Qj", "Qk", "A", "Estado", "Dest."};
		Object[][] estacaoReservaData = populateData("estacao");
		_estacaoReserva = new DynamicTableGUI("Estação Reserva", estacaoReservaTitle, estacaoReservaData, xPos, yPos);
		xPos += _estacaoReserva.getWidth();
		String[] registradoresTitle = new String[] {"Nome", "Qi", "Vi"};
		Object[][] registradoresData = populateData("registradores"); 
		_registradores = new DynamicTableGUI ("Registradores", registradoresTitle, registradoresData, xPos, yPos);
		xPos += _registradores.getWidth();
		String[] bufferReordTitle = new String[] {"Instrução", "Estado", "Destino", "Resultado", "Pronto", "isIn", "Tipo", "Branched"};
		Object[][] bufferReordData = populateData("rob");
		_bufferReord = new DynamicTableGUI ("Buffer de Reordenação", bufferReordTitle, bufferReordData, xPos, yPos);
		
		xPos = 0;
		yPos = _bufferReord.getHeight();
		String[] memoryTitle = new String[] {"Endereço", "Valor Bin", "Valor Int"};
		Object[][] memoryData =  populateData("memory"); 
		_memory = new DynamicTableGUI ("Memória Recente Usada", memoryTitle, memoryData, xPos, yPos);
		xPos += _memory.getWidth();
		String[] statusTitle = new String[] {"Clock corrente", "PC", "Número de Instruções Concluídas", "CPI"};
		Object[][] statusData = populateData("status"); 
		_status = new DynamicTableGUI ("Status", statusTitle, statusData, xPos, yPos);
	
 	}
	private Object[][] populateData(String string) {
		Object[][] data = null;
		if (string == "estacao") {
			data = new Object [Arch.getNumberOfRS()][];
			for (int i = 0; i < Arch.getNumberOfRS(); i++) {
				data[i] = Arch.getRS()[i].getInfo();
			}
		} else if (string == "registradores") {
			data = new Object[32][];
			for (int i = 0; i < 32; i++) {
				data[i] = Arch.RegisterStat.getInfo(i);
			}
		} else if (string == "status") {
			data = new Object[1][];
			data[0] = Arch.getProgramInfo();
		} else if (string == "memory") {
			data = Arch.Mem.getMemoryInfo();
		} else if (string == "rob") {
			data = Arch.getReorderBuffer().getListInfo();
		}
		return data;
	}
	public void runCycle() {
		Object[][] estacaoData = populateData("estacao");
		_estacaoReserva.updateTable(estacaoData);
		
		Object[][] registradoresData = populateData("registradores"); 
		_registradores.updateTable(registradoresData);
		
		Object[][] bufferReordData = populateData("rob");
		_bufferReord.updateTable(bufferReordData);
		
		Object[][] memoryData =  populateData("memory"); 
		_memory.updateTable(memoryData);
		
		Object[][] statusData = populateData("status");
		_status.updateTable(statusData);
		
	}
	public void setRunning(boolean b) {
		
		
	}
	public void removeFrames() {
		_memory.dispose();
		_status.dispose();
		_estacaoReserva.dispose();
		_status.dispose();
		_registradores.dispose();
		
	}
}
