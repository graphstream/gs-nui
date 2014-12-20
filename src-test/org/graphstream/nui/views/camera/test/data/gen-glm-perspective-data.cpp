#include <glm/mat4x4.hpp> // glm::mat4
#include <glm/vec4.hpp>
#include <glm/ext.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/detail/func_geometric.hpp>
#include <GL/gl.h>
#include <iostream>
#include <fstream>
#include <random>
#include <stdexcept>
#include <string>
#include <chrono>

#define PRINTD(d, out) out.write(reinterpret_cast<const char *>(&d), sizeof(double));
#define PRINTM(mat, out) out.write(reinterpret_cast<const char *>(glm::value_ptr(mat)), 16*sizeof(double));
#define PRINTV(vec, out) out.write(reinterpret_cast<const char *>(&vec.x), vec.length()*sizeof(double));

//
// Compil with :
// > gcc -lm -lstdc++ -o test-glm -std=gnu++11 $FILE
//

int main(int argc, char** argv) {
	unsigned seed = std::chrono::system_clock::now().time_since_epoch().count();
	std::default_random_engine gen (seed);
	std::uniform_real_distribution<double> d(0.01,64.0);
	std::uniform_real_distribution<double> da(0.1, 2.0);
	std::uniform_real_distribution<double> df(3.14 / 4.0, 3.14);
	
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
		double fovy   = df(gen);
		double aspect = da(gen);
		double zNear  = d(gen);
		double zFar   = zNear + d(gen);
		
		glm::dmat4 mat = glm::perspective(fovy, aspect, zNear, zFar);
		
		PRINTD(fovy,   fout)
		PRINTD(aspect, fout)
		PRINTD(zNear,  fout)
		PRINTD(zFar,   fout)
		PRINTM(mat,    fout)
	}
	
	fout.close();
	
	return 0;
}

