#include <glm/mat4x4.hpp> // glm::mat4
#include <glm/ext.hpp>
#include <GL/gl.h>
#include <fstream>
#include <iostream>
#include <random>
#include <stdexcept>
#include <string>
#include <chrono>

#define PRINTM(mat, out) out.write(reinterpret_cast<const char *>(glm::value_ptr(mat)), 16*sizeof(double));
#define PRINTV(vec, out) out.write(reinterpret_cast<const char *>(&vec.x), vec.length()*sizeof(double));

//
// Compil with :
// > gcc -lm -lstdc++ -o test-glm -std=gnu++11 test-glm.cpp
//

int main(int argc, char** argv) {
	unsigned seed = std::chrono::system_clock::now().time_since_epoch().count();
	std::default_random_engine gen (seed);
	std::uniform_real_distribution<double> d(-64.0,64.0);
	
	int maxIte = 100;
	
	if (argc < 2) {
		std::cerr << "Usage: " << argv[0] << " file [count]" << std::endl;
		return 1;
	}
	
	std::ofstream fout;
	fout.open (argv[1], std::ios::binary);
	
	if (argc > 2) {
		try {
    		maxIte = std::stoi(std::string(argv[2]));
		}
		catch (const std::invalid_argument& ia) {
			std::cerr << "Invalid argument: " << ia.what() << '\n';
			return 1;
		}
	}
	
	for (int ite = 0; ite < maxIte; ite++) {
		glm::dmat4 mat(
			d(gen), d(gen), d(gen), d(gen),
			d(gen), d(gen), d(gen), d(gen),
			d(gen), d(gen), d(gen), d(gen),
			d(gen), d(gen), d(gen), d(gen));
	
		glm::dmat4 inv = glm::inverse(mat);
	
		PRINTM(mat, fout)
		PRINTM(inv, fout)
	}
	
	fout.close();
	
	return 0;
}

